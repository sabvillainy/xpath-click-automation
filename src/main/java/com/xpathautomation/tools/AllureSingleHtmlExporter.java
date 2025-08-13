package com.xpathautomation.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AllureSingleHtmlExporter
 *
 * Purpose: Convert an existing allure-report folder into a single,
 * self-contained
 * HTML file that can be opened directly (file://) without starting a local
 * server.
 *
 * Approach: Reads textual assets (JSON/CSV/TXT/HTML) from common Allure folders
 * (data, widgets, export, history) and embeds them into index.html as a
 * window.__ALLURE_EMBEDDED__ map. Then it injects a small script that overrides
 * window.fetch to serve responses from the embedded map. This bypasses browser
 * restrictions for fetch on file:// scheme.
 *
 * Usage (from project root):
 * mvn -q -DskipTests compile
 * java -cp target/classes com.xpathautomation.tools.AllureSingleHtmlExporter
 * "allure-report" "AllureReport.html"
 *
 * The resulting AllureReport.html can be emailed and opened directly by users
 * without any setup, IP, or .bat files.
 */
public final class AllureSingleHtmlExporter {

    private static final List<String> TEXT_EXTENSIONS = Arrays.asList(
            ".json", ".csv", ".txt", ".html");

    private AllureSingleHtmlExporter() {
    }

    public static void main(String[] args) throws Exception {
        Path reportDirectory = Paths.get(args.length > 0 ? args[0] : "allure-report").toAbsolutePath();
        Path indexHtmlPath = reportDirectory.resolve("index.html");
        if (!Files.exists(indexHtmlPath)) {
            System.err.println("index.html bulunamadı: " + indexHtmlPath);
            System.exit(1);
        }

        String indexHtml = Files.readString(indexHtmlPath, StandardCharsets.UTF_8);

        // Typical Allure content folders we want to embed
        List<String> includeRoots = Arrays.asList("data", "widgets", "export", "history");

        Map<String, String> embeddedTextByRelativePath = new LinkedHashMap<>();

        for (String root : includeRoots) {
            Path rootPath = reportDirectory.resolve(root);
            if (!Files.exists(rootPath)) {
                continue;
            }
            try (Stream<Path> pathStream = Files.walk(rootPath)) {
                for (Path filePath : pathStream.filter(Files::isRegularFile).collect(Collectors.toList())) {
                    if (!isTextualFile(filePath)) {
                        // Skip non-textual files (e.g., images); they remain referenced by relative
                        // paths
                        continue;
                    }
                    String relative = reportDirectory.relativize(filePath).toString().replace('\\', '/');
                    String content = Files.readString(filePath, StandardCharsets.UTF_8);
                    // Prevent breaking out of JS string context
                    content = content.replace("\u2028", "\\u2028").replace("\u2029", "\\u2029");
                    embeddedTextByRelativePath.put(relative, content);
                }
            }
        }

        String embeddedMapJson = buildEmbeddedMapJson(embeddedTextByRelativePath);
        String injectionScript = buildInjectionScript(embeddedMapJson);

        // First, inline local CSS/JS assets referenced by link/script tags
        String htmlWithInlinedAssets = inlineLocalAssets(indexHtml, reportDirectory, injectionScript);

        // If nothing matched (unexpected layout), ensure the injection is still present
        String outputHtml = ensureInjectionPresent(htmlWithInlinedAssets, injectionScript);

        Path outPath = Paths.get(args.length > 1 ? args[1] : "AllureReport.html").toAbsolutePath();
        ensureParentDirectoryExists(outPath);
        Files.writeString(outPath, outputHtml, StandardCharsets.UTF_8);

        System.out.println("Tek dosya Allure raporu hazır: " + outPath);
        System.out.println("Bu dosyayı çift tıklayarak açabilirsiniz. Sunucu/kurulum gerekmez.");
    }

    private static boolean isTextualFile(Path filePath) {
        String lowercaseName = filePath.getFileName().toString().toLowerCase(Locale.ROOT);
        for (String ext : TEXT_EXTENSIONS) {
            if (lowercaseName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private static String buildEmbeddedMapJson(Map<String, String> data) {
        StringBuilder mapBuilder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) {
                mapBuilder.append(',');
            }
            first = false;
            mapBuilder.append('"').append(escapeJsonString(entry.getKey())).append('"').append(':')
                    .append(escapeAsJsString(entry.getValue()));
        }
        mapBuilder.append('}');
        return mapBuilder.toString();
    }

    private static String buildInjectionScript(String mapJson) {
        return "<script>\n" +
                "window.__ALLURE_EMBEDDED__ = " + mapJson + ";\n" +
                "(function(){\n" +
                "  const embedded = window.__ALLURE_EMBEDDED__ || {};\n" +
                "  const originalFetch = window.fetch ? window.fetch.bind(window) : null;\n" +
                "  const roots = ['data/', 'widgets/', 'export/', 'history/'];\n" +
                "  function normalizeKey(input){\n" +
                "    try {\n" +
                "      const u = new URL(input, window.location.href);\n" +
                "      input = (u.pathname || input) + (u.search || '');\n" +
                "    } catch(e) { }\n" +
                "    if (input.indexOf('?') !== -1) input = input.split('?')[0];\n" +
                "    if (input.indexOf('#') !== -1) input = input.split('#')[0];\n" +
                "    for (var i=0;i<roots.length;i++){\n" +
                "      var idx = input.lastIndexOf(roots[i]);\n" +
                "      if (idx !== -1){ input = input.substring(idx); break; }\n" +
                "    }\n" +
                "    if (input.startsWith('./')) input = input.slice(2);\n" +
                "    if (input.startsWith('/')) input = input.slice(1);\n" +
                "    return input;\n" +
                "  }\n" +
                "  window.fetch = function(resource, init){\n" +
                "    try {\n" +
                "      const raw = typeof resource === 'string' ? resource : (resource && resource.url) || '';\n" +
                "      const key = normalizeKey(raw);\n" +
                "      if (Object.prototype.hasOwnProperty.call(embedded, key)) {\n" +
                "        const body = embedded[key];\n" +
                "        const ct = key.endsWith('.json') ? 'application/json'\n" +
                "                 : key.endsWith('.csv') ? 'text/csv'\n" +
                "                 : key.endsWith('.txt') ? 'text/plain' : 'text/html';\n" +
                "        return Promise.resolve(new Response(body, { status: 200, headers: { 'Content-Type': ct } }));\n"
                +
                "      }\n" +
                "    } catch (e) { }\n" +
                "    if (originalFetch) return originalFetch(resource, init);\n" +
                "    return Promise.reject(new Error('fetch unsupported in this environment'));\n" +
                "  };\n" +
                "})();\n" +
                "</script>\n";
    }

    private static String ensureInjectionPresent(String html, String injection) {
        if (html.contains("__ALLURE_EMBEDDED__")) {
            return html;
        }
        int headCloseIndex = html.indexOf("</head>");
        if (headCloseIndex >= 0) {
            return html.substring(0, headCloseIndex) + injection + html.substring(headCloseIndex);
        }
        return injection + html;
    }

    private static String inlineLocalAssets(String html, Path reportDirectory, String injection) throws IOException {
        String result = html;

        // Inline CSS: <link rel="stylesheet" ... href="...">
        Pattern cssPattern = Pattern.compile("<link\\s+[^>]*rel=\\\"stylesheet\\\"[^>]*href=\\\"([^\\\"]+)\\\"[^>]*/?>",
                Pattern.CASE_INSENSITIVE);
        Matcher cssMatcher = cssPattern.matcher(result);
        StringBuffer cssBuffer = new StringBuffer();
        while (cssMatcher.find()) {
            String href = cssMatcher.group(1);
            if (href.startsWith("http://") || href.startsWith("https://")) {
                cssMatcher.appendReplacement(cssBuffer, Matcher.quoteReplacement(cssMatcher.group()));
                continue;
            }
            Path cssPath = reportDirectory.resolve(href).normalize();
            if (Files.exists(cssPath)) {
                String cssContent = Files.readString(cssPath, StandardCharsets.UTF_8);
                String replacement = "<style>\n" + cssContent + "\n</style>";
                cssMatcher.appendReplacement(cssBuffer, Matcher.quoteReplacement(replacement));
            } else {
                cssMatcher.appendReplacement(cssBuffer, Matcher.quoteReplacement(cssMatcher.group()));
            }
        }
        cssMatcher.appendTail(cssBuffer);
        result = cssBuffer.toString();

        // Inline JS: <script src="..."></script>
        Pattern jsPattern = Pattern.compile("<script\\s+[^>]*src=\\\"([^\\\"]+)\\\"[^>]*>\\s*</script>",
                Pattern.CASE_INSENSITIVE);
        Matcher jsMatcher = jsPattern.matcher(result);
        StringBuffer jsBuffer = new StringBuffer();
        while (jsMatcher.find()) {
            String src = jsMatcher.group(1);
            if (src.startsWith("http://") || src.startsWith("https://")) {
                jsMatcher.appendReplacement(jsBuffer, Matcher.quoteReplacement(jsMatcher.group()));
                continue;
            }
            Path jsPath = reportDirectory.resolve(src).normalize();
            if (Files.exists(jsPath)) {
                String jsContent = Files.readString(jsPath, StandardCharsets.UTF_8);
                String inlineScript = "<script>\n" + jsContent + "\n</script>";
                // Special-case app.js: inject our fetch override BEFORE app.js runs
                if (src.equals("app.js")) {
                    inlineScript = injection + inlineScript;
                }
                jsMatcher.appendReplacement(jsBuffer, Matcher.quoteReplacement(inlineScript));
            } else {
                jsMatcher.appendReplacement(jsBuffer, Matcher.quoteReplacement(jsMatcher.group()));
            }
        }
        jsMatcher.appendTail(jsBuffer);
        result = jsBuffer.toString();

        return result;
    }

    private static void ensureParentDirectoryExists(Path outPath) throws IOException {
        Path parent = outPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static String escapeJsonString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String escapeAsJsString(String s) {
        StringBuilder builder = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        builder.append(String.format("\\u%04x", (int) c));
                    } else {
                        builder.append(c);
                    }
            }
        }
        builder.append('"');
        return builder.toString();
    }
}
