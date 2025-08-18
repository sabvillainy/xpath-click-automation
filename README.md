# XPath Click Automation

Java CLI application that uses Selenium WebDriver to navigate to a URL and click elements found by XPath, in the order you provide.

## Features

- 🎯 XPath element finding and clicking
- 🌐 Chrome WebDriver support
- ⏱️ Automatic wait times (customizable)
- 🛡️ Error handling and reporting
- 📦 Maven project management
- 🔄 WebDriverManager for automatic driver management
- 📊 Allure reporting support via tests (steps and screenshots)

## Requirements

- Java 24 or higher
- Maven 3.6 or higher
- Chrome browser (WebDriverManager will automatically download ChromeDriver)

## Build

```bash
mvn -q -DskipTests package
```

The runnable jar is produced at `target/xpath-click-automation-1.0.0.jar`.

## Usage (CLI)

Minimum (one XPath):
```bash
java -jar target/xpath-click-automation-1.0.0.jar --url "https://example.com" --xpath "//button[@id='go']"
```

Multiple XPaths (executed in order):
```bash
java -jar target/xpath-click-automation-1.0.0.jar \
  --url "https://www.w3schools.com/html/html_forms.asp" \
  --xpath "//input[@type='text']" \
  --xpath "//input[@type='submit']"
```

Optional flags:
- `--headless` run without opening a browser window
- `--delay <ms>` delay between clicks in milliseconds (default: 1000)
- `--timeout <s>` wait timeout in seconds (default: 10)

Help:
```bash
java -jar target/xpath-click-automation-1.0.0.jar --help
```

### Notes

- Keep the entire XPath in quotes. If you must include double quotes inside the XPath, the app automatically converts them to single quotes at runtime.
- WebDriverManager downloads the matching ChromeDriver automatically.

## Allure Report (via tests)

The project includes JUnit 5 tests with Allure annotations and automatic screenshots.

1) Run tests (this generates Allure results under `target/allure-results`):
```bash
mvn test
```

2) Generate the Allure HTML report:
```bash
mvn allure:report
```

The report is generated at `target/site/allure-maven-plugin/index.html`.

## Build and Test

```bash
mvn clean package
```

## Examples

## Examples (CLI)

Google Search (two steps):
```bash
java -jar target/xpath-click-automation-1.0.0.jar \
  --url "https://www.google.com" \
  --xpath "//input[@name='q']" \
  --xpath "//button[@name='btnK']"
```

### Clicking Menu Items on a Website
```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://example.com")
    .addXPath("//nav//a[contains(text(),'About')]")
    .addXPath("//nav//a[contains(text(),'Contact')]")
    .setClickDelay(2000);

automation.run();
automation.closeDriver();
```

### Form Filling (Headless Mode)
```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://example.com/form")
    .addXPath("//input[@id='name']")
    .addXPath("//input[@id='email']")
    .addXPath("//button[@type='submit']")
    .setHeadless(true)
    .setWaitTimeout(20);

automation.run();
automation.closeDriver();
```

### XPath with Double Quotes
```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://example.com")
    .addXPath("//*[contains(text(),\"The <form> Element\")]"); // Automatically fixed

automation.run();
automation.closeDriver();
```

## XPath Examples

| Description | XPath |
|-------------|-------|
| Element by ID | `//input[@id='username']` |
| Element by class | `//button[@class='btn-primary']` |
| Element containing text | `//a[contains(text(),'Click')]` |
| Nested element | `//div[@class='container']//button` |
| Element by attribute | `//input[@type='submit']` |
| **With double quotes** | `//*[contains(text(),"The <form> Element")]` |

## Project Structure

```
src/
  main/java/com/xpathautomation/
    XPathClickAutomation.java
  test/java/com/xpathautomation/
    support/
      AllureUtils.java
      AllureTestWatcher.java
    XPathClickAutomationAllureIT.java
    XPathClickAutomationTest.java
pom.xml
README.md
target/
  xpath-click-automation-1.0.0.jar
```

## Testing

To run tests:

```bash
mvn test
```

## Error Handling

The application handles the following error conditions:

- **Element not found**: If an element matching the XPath is not found, an error message is printed but the program continues
- **WebDriver error**: If WebDriver cannot be started, the program terminates
- **URL error**: If an invalid URL is attempted, an error message is printed
- **Network error**: If there's no internet connection, an appropriate error message is shown
- **XPath quote problem**: Double quotes are automatically converted to single quotes
- **Configuration error**: If URL or XPath is not set, an appropriate error message is shown

## Output Example (CLI)

```
=== XPath Click Automation (CLI) ===
Chrome WebDriver successfully started.
Navigating to URL: https://www.google.com
Page successfully loaded.
Total 2 XPath(s) to process.

--- Processing XPath 1/2 ---
Searching for element with XPath: //input[@name='q']
Element successfully clicked: //input[@name='q']

--- Processing XPath 2/2 ---
Searching for element with XPath: //button[@name='btnK']
Element successfully clicked: //button[@name='btnK']

=== Process Completed ===
Successful clicks: 2
Failed clicks: 0
Total XPaths: 2
WebDriver successfully closed.
```

## Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Contact

For questions about the project, please open an issue.

## Version History

- **v1.1.0**: Programmatic usage added - Parameter configuration through code, method chaining support
- **v1.0.1**: XPath quote problem solution added - Automatic conversion of double quotes to single quotes
- **v1.0.0**: Initial version - Basic XPath clicking functionality