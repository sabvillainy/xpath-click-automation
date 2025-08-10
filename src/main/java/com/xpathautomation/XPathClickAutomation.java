package com.xpathautomation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * XPath Click Automation - Selenium kullanarak XPath ile element bulma ve
 * tıklama uygulaması
 * 
 * Bu sınıf, verilen URL'ye gidip belirtilen XPath'lerle eşleşen elementleri
 * bulur ve tıklar.
 * Her tıklamadan sonra 1 saniye bekler ve hata durumlarını yönetir.
 * 
 * @author XPath Automation Team
 * @version 1.0.0
 */
public class XPathClickAutomation {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int CLICK_DELAY_MS = 1000; // 1 saniye
    private static final int WAIT_TIMEOUT_SECONDS = 10;

    // Konfigürasyon parametreleri
    private String targetUrl;
    private List<String> xpaths;
    private boolean headless = false;
    private int clickDelayMs = CLICK_DELAY_MS;
    private int waitTimeoutSeconds = WAIT_TIMEOUT_SECONDS;

    /**
     * Constructor - WebDriver'ı başlatır ve yapılandırır
     */
    public XPathClickAutomation() {
        initializeDriver();
    }

    /**
     * URL'yi ayarlar
     * 
     * @param url Hedef URL
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation setUrl(String url) {
        this.targetUrl = url;
        return this;
    }

    /**
     * XPath listesini ayarlar
     * 
     * @param xpaths XPath listesi
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation setXPaths(List<String> xpaths) {
        this.xpaths = new ArrayList<>(xpaths);
        return this;
    }

    /**
     * Tek bir XPath ekler
     * 
     * @param xpath Eklenecek XPath
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation addXPath(String xpath) {
        if (this.xpaths == null) {
            this.xpaths = new ArrayList<>();
        }
        this.xpaths.add(xpath);
        return this;
    }

    /**
     * Headless modu ayarlar
     * 
     * @param headless true ise headless modda çalışır
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation setHeadless(boolean headless) {
        this.headless = headless;
        return this;
    }

    /**
     * Tıklama gecikmesini ayarlar
     * 
     * @param clickDelayMs Milisaniye cinsinden gecikme
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation setClickDelay(int clickDelayMs) {
        this.clickDelayMs = clickDelayMs;
        return this;
    }

    /**
     * Bekleme süresini ayarlar
     * 
     * @param waitTimeoutSeconds Saniye cinsinden bekleme süresi
     * @return Bu nesne (method chaining için)
     */
    public XPathClickAutomation setWaitTimeout(int waitTimeoutSeconds) {
        this.waitTimeoutSeconds = waitTimeoutSeconds;
        return this;
    }

    /**
     * Chrome WebDriver'ı başlatır ve yapılandırır
     */
    private void initializeDriver() {
        try {
            // WebDriverManager ile ChromeDriver'ı otomatik olarak indir ve yapılandır
            WebDriverManager.chromedriver().setup();

            // Chrome seçeneklerini yapılandır
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized"); // Pencereyi maksimize et
            options.addArguments("--disable-blink-features=AutomationControlled"); // Otomasyon tespitini engelle
            options.addArguments("--disable-extensions"); // Uzantıları devre dışı bırak

            // Headless mod ayarı
            if (headless) {
                options.addArguments("--headless");
            }

            // WebDriver'ı başlat
            driver = new ChromeDriver(options);

            // WebDriverWait'i yapılandır
            wait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeoutSeconds));

            System.out.println("Chrome WebDriver başarıyla başlatıldı.");

        } catch (Exception e) {
            System.err.println("WebDriver başlatılırken hata oluştu: " + e.getMessage());
            throw new RuntimeException("WebDriver başlatılamadı", e);
        }
    }

    /**
     * Verilen URL'ye gider
     * 
     * @param url Gidilecek web sitesinin URL'si
     */
    public void navigateToUrl(String url) {
        try {
            System.out.println("URL'ye gidiliyor: " + url);
            driver.get(url);

            // Sayfanın yüklenmesini bekle
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
            System.out.println("Sayfa başarıyla yüklendi.");

        } catch (Exception e) {
            System.err.println("URL'ye gidilirken hata oluştu: " + e.getMessage());
            throw new RuntimeException("URL'ye gidilemedi: " + url, e);
        }
    }

    /**
     * Verilen XPath ile element bulur ve tıklar
     * 
     * @param xpath Bulunacak elementin XPath'i
     * @return true eğer element bulundu ve tıklandıysa, false aksi takdirde
     */
    public boolean clickElementByXPath(String xpath) {
        try {
            System.out.println("XPath ile element aranıyor: " + xpath);

            // Elementin görünür olmasını bekle
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

            // Elemente tıkla
            element.click();
            System.out.println("Element başarıyla tıklandı: " + xpath);

            // Tıklamadan sonra belirtilen süre bekle
            Thread.sleep(clickDelayMs);

            return true;

        } catch (NoSuchElementException e) {
            System.err.println("XPath ile element bulunamadı: " + xpath);
            return false;
        } catch (Exception e) {
            System.err.println("Element tıklanırken hata oluştu (" + xpath + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Birden fazla XPath ile element bulur ve tıklar
     * 
     * @param xpaths Tıklanacak elementlerin XPath'lerinin listesi
     */
    public void clickElementsByXPaths(List<String> xpaths) {
        if (xpaths == null || xpaths.isEmpty()) {
            System.err.println("XPath listesi boş veya null!");
            return;
        }

        System.out.println("Toplam " + xpaths.size() + " XPath işlenecek.");

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < xpaths.size(); i++) {
            String xpath = xpaths.get(i);
            System.out.println("\n--- XPath " + (i + 1) + "/" + xpaths.size() + " işleniyor ---");

            if (clickElementByXPath(xpath)) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        System.out.println("\n=== İşlem Tamamlandı ===");
        System.out.println("Başarılı tıklamalar: " + successCount);
        System.out.println("Başarısız tıklamalar: " + failureCount);
        System.out.println("Toplam XPath: " + xpaths.size());
    }

    /**
     * Konfigürasyonu kontrol eder ve otomasyonu çalıştırır
     */
    public void run() {
        if (targetUrl == null || targetUrl.isEmpty()) {
            throw new IllegalArgumentException("URL ayarlanmamış! setUrl() metodunu kullanın.");
        }

        if (xpaths == null || xpaths.isEmpty()) {
            throw new IllegalArgumentException("XPath listesi boş! setXPaths() veya addXPath() metodunu kullanın.");
        }

        try {
            // URL'ye git
            navigateToUrl(targetUrl);

            // XPath'leri düzelt ve işle
            List<String> fixedXpaths = xpaths.stream()
                    .map(XPathClickAutomation::fixXPathQuotes)
                    .toList();

            clickElementsByXPaths(fixedXpaths);

        } catch (Exception e) {
            System.err.println("Otomasyon çalışırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * WebDriver'ı kapatır ve kaynakları temizler
     */
    public void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("WebDriver başarıyla kapatıldı.");
            } catch (Exception e) {
                System.err.println("WebDriver kapatılırken hata oluştu: " + e.getMessage());
            }
        }
    }

    /**
     * XPath'teki çift tırnakları tek tırnağa çevirir
     * Bu metod, XPath'te çift tırnak içeren string'leri otomatik olarak düzeltir
     * 
     * @param xpath Orijinal XPath string'i
     * @return Düzeltilmiş XPath string'i
     */
    private static String fixXPathQuotes(String xpath) {
        if (xpath == null || xpath.isEmpty()) {
            return xpath;
        }

        // Çift tırnakları tek tırnağa çevir
        return xpath.replace("\"", "'");
    }

    /**
     * Ana metod - Örnek kullanım
     * 
     * @param args Komut satırı argümanları (artık kullanılmıyor)
     */
    public static void main(String[] args) {
        System.out.println("=== XPath Click Automation ===");

        // https://www.w3schools.com/html/html_forms.asp "//input[@type='text']" "//input[@type='submit']" "//*[contains(text(),'The <form> Element')]" "dasdasd"
        // Örnek kullanım 1: Method chaining ile
        XPathClickAutomation automation1 = new XPathClickAutomation()
                .setUrl("https://www.w3schools.com/html/html_forms.asp")
                .addXPath("//input[@type='text']")
                .addXPath("//input[@type='submit']")
                .addXPath("//*[contains(text(),'The <form> Element')]")
                .addXPath("asdasd")
                .setHeadless(false)
                .setClickDelay(2000); // 2 saniye bekle

        try {
            automation1.run();
        } finally {
            automation1.closeDriver();
        }


        /* Örnek kullanım 2: Ayrı ayrı ayarlama

        System.out.println("\n=== Örnek 2: Ayrı ayrı ayarlama ===");

        XPathClickAutomation automation2 = new XPathClickAutomation();
        automation2.setUrl("https://example.com");
        automation2.setXPaths(List.of(
                "//nav//a[contains(text(),'Hakkımızda')]",
                "//nav//a[contains(text(),'İletişim')]"));
        automation2.setHeadless(true); // Headless modda çalıştır
        automation2.setWaitTimeout(15); // 15 saniye bekle

        try {
            automation2.run();
        } finally {
            automation2.closeDriver();
        }*/
    }
}