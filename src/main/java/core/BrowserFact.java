package core;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.concurrent.TimeUnit;

public class BrowserFact extends MethodsFact {

    @Parameters("browser")
    @BeforeTest
    public void setUp(@Optional("CH") String browser) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\danylok\\Downloads\\chromedriver_win32\\chromedriver.exe");
        if (browser.equals("CH")) {
            driver = new ChromeDriver();
        } else if (browser.equals("FF")) {
            driver = new FirefoxDriver();
        } else if (browser.equals("OP")) {
            driver = new OperaDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
    }

    @AfterTest
    public void tearDown() {
//        driver.close();
//        driver.quit();
    }
}
