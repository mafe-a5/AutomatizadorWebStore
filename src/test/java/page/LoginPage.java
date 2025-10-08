package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Selectores actualizados según el HTML real
    By emailField = By.id("field-email");
    By passwordField = By.id("field-password");
    By loginButton = By.id("submit-login");

    public void goToStore() {
        // Ir directamente a la página de login
        driver.get("https://qalab.bensg.com/store/en/login?back=my-account");
    }

    public void login(String user, String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(user);
        driver.findElement(passwordField).sendKeys(pass);
        driver.findElement(loginButton).click();
    }

    public boolean isLoginSuccessful() {
        // Verifica que haya entrado al área de cuenta
        return driver.getPageSource().contains("Welcome to your account");
    }

    public boolean isLoginFailed() {
        // Verifica si se muestra un mensaje de error
        return driver.getPageSource().contains("Authentication failed") ||
                driver.getPageSource().contains("Invalid credentials");
    }
}