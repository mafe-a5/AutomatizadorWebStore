package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    }

    By emailField = By.id("email");
    By passwordField = By.id("passwd");
    By loginButton = By.id("SubmitLogin");

    public void goToStore() {
        driver.get("https://qalab.bensg.com/store");
    }

    public void login(String user, String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(user);
        driver.findElement(passwordField).sendKeys(pass);
        driver.findElement(loginButton).click();
    }

    public boolean isLoginSuccessful() {
        return driver.getTitle().contains("My account");
    }

    public boolean isLoginFailed() {
        return driver.getPageSource().contains("Authentication failed");
    }
}