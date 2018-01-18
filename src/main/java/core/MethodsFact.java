package core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MethodsFact {

    public static WebDriver driver;
    public static final int DEFAULT_WAIT_TIME = 30;

    public boolean waitForUrlContains(String text, int sec){
        for(int i = 0; i<=sec*10; i++){
            if(driver.getCurrentUrl().contains(text)) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isElementPresent(By by, int sec) {
        driver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);
        List<WebElement> elementList = driver.findElements(by);
        driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
        return elementList.size() > 0;
    }

    public static void type(By locator, String text) {
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(text);
    }

    public static void click(By locator) {
        driver.findElement(locator).click();
    }
}
