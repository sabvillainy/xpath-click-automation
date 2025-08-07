# XPath Click Automation

This project is a Java command-line application that uses Selenium WebDriver to find and click HTML elements specified by XPath on web sites.

## Features

- 🎯 XPath element finding and clicking
- 🌐 Chrome WebDriver support
- ⏱️ Automatic wait times (customizable)
- 🛡️ Error handling and reporting
- 📦 Maven project management
- 🔄 WebDriverManager for automatic driver management
- 🎛️ **Programmatic usage** - Parameter configuration through code

## Requirements

- Java 24 or higher
- Maven 3.6 or higher
- Chrome browser (WebDriverManager will automatically download ChromeDriver)

## Installation

1. Clone the project:
```bash
git clone <repository-url>
cd xpath-click-automation
```

2. Download Maven dependencies:
```bash
mvn clean install
```

## Usage

### 🎛️ Programmatic Usage (Recommended)

You can now configure parameters through code instead of command line arguments:

#### Method Chaining Usage

```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://www.google.com")
    .addXPath("//input[@name='q']")
    .addXPath("//button[@name='btnK']")
    .setHeadless(false)
    .setClickDelay(2000) // Wait 2 seconds
    .setWaitTimeout(15); // 15 seconds timeout

try {
    automation.run();
} finally {
    automation.closeDriver();
}
```

#### Separate Configuration

```java
XPathClickAutomation automation = new XPathClickAutomation();
automation.setUrl("https://example.com");
automation.setXPaths(List.of(
    "//nav//a[contains(text(),'About')]",
    "//nav//a[contains(text(),'Contact')]"
));
automation.setHeadless(true); // Run in headless mode
automation.setWaitTimeout(15); // Wait 15 seconds

try {
    automation.run();
} finally {
    automation.closeDriver();
}
```

#### Single XPath Addition

```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://example.com")
    .addXPath("//button[@id='submit']")
    .addXPath("//input[@name='username']")
    .addXPath("//input[@name='password']");

automation.run();
automation.closeDriver();
```

### 🔧 Available Methods

| Method | Description | Example |
|--------|-------------|---------|
| `setUrl(String url)` | Sets the target URL | `.setUrl("https://example.com")` |
| `setXPaths(List<String> xpaths)` | Sets the XPath list | `.setXPaths(List.of("//button", "//input"))` |
| `addXPath(String xpath)` | Adds a single XPath | `.addXPath("//button[@id='submit']")` |
| `setHeadless(boolean headless)` | Sets headless mode | `.setHeadless(true)` |
| `setClickDelay(int ms)` | Sets click delay | `.setClickDelay(2000)` |
| `setWaitTimeout(int seconds)` | Sets wait timeout | `.setWaitTimeout(15)` |
| `run()` | Runs the automation | `automation.run()` |
| `closeDriver()` | Closes the WebDriver | `automation.closeDriver()` |

### 📝 IntelliJ IDEA Usage

1. Open `XPathClickAutomation.java` file
2. Find the `main` method
3. Modify the example code according to your needs
4. Click the Run button

**Example main method:**
```java
public static void main(String[] args) {
    XPathClickAutomation automation = new XPathClickAutomation()
        .setUrl("https://www.google.com")
        .addXPath("//input[@name='q']")
        .addXPath("//button[@name='btnK']")
        .setHeadless(false)
        .setClickDelay(1000);

    try {
        automation.run();
    } finally {
        automation.closeDriver();
    }
}
```

### Running with Maven

```bash
# Compile and run the project
mvn clean compile exec:java -Dexec.mainClass="com.xpathautomation.XPathClickAutomation"
```

### Running with JAR File

1. First, create the JAR file:
```bash
mvn clean package
```

2. Run the created JAR file:
```bash
java -jar target/xpath-click-automation-1.0.0.jar
```

## Examples

### Google Search
```java
XPathClickAutomation automation = new XPathClickAutomation()
    .setUrl("https://www.google.com")
    .addXPath("//input[@name='q']")
    .addXPath("//button[@name='btnK']");

automation.run();
automation.closeDriver();
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
xpath-click-automation/
├── src/
│   ├── main/java/com/xpathautomation/
│   │   └── XPathClickAutomation.java
│   └── test/java/com/xpathautomation/
│       └── XPathClickAutomationTest.java
├── pom.xml
├── README.md
└── target/
    └── xpath-click-automation-1.0.0.jar
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

## Output Example

```
=== XPath Click Automation - Programmatic Usage ===
Chrome WebDriver successfully started.
Navigating to URL: https://www.google.com
Page successfully loaded.
Total 2 XPath to process.

--- Processing XPath 1/2 ---
Searching for element with XPath: //input[@name='q']
Element successfully clicked: //input[@name='q']

--- Processing XPath 2/2 ---
Searching for element with XPath: //button[@name='btnK']
Element successfully clicked: //button[@name='btnK']

=== Process Completed ===
Successful clicks: 2
Failed clicks: 0
Total XPath: 2
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