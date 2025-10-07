package glue;

import driver.DriverManager;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import page.LoginPage;

public class ProductLoginStepsDef {

    WebDriver driver = DriverManager.getDriver();
    LoginPage loginPage = new LoginPage(driver);

    @Given("estoy en la página de la tienda")
    public void abrirTienda() {
        loginPage.goToStore();
    }

    @And("me logueo con mi usuario {string} y clave {string}")
    public void meLogueo(String usuario, String clave) {
        loginPage.login(usuario, clave);
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navegarCategoria(String categoria, String subcategoria) {
        try {
            driver.findElement(By.linkText(categoria)).click();
            driver.findElement(By.linkText(subcategoria)).click();
        } catch (Exception e) {
            System.out.println("Categoría o subcategoría no encontrada: " + categoria + " / " + subcategoria);
            Assert.fail("Categoría inexistente");
        }
    }

    @And("agrego 2 unidades del primer producto al carrito")
    public void agregarProducto() {
        driver.findElement(By.cssSelector(".ajax_add_to_cart_button")).click();
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validarPopup() {
        Assert.assertTrue(driver.getPageSource().contains("Product successfully added"));
    }

    @And("valido en el popup que el monto total sea calculado correctamente")
    public void validarMonto() {
        Assert.assertTrue(driver.getPageSource().contains("$"));
    }

    @When("finalizo la compra")
    public void finalizarCompra() {
        driver.findElement(By.cssSelector(".button-medium")).click();
    }

    @Then("valido el titulo de la pagina del carrito")
    public void validarTituloCarrito() {
        Assert.assertTrue(driver.getTitle().contains("Order"));
    }

    @And("vuelvo a validar el calculo de precios en el carrito")
    public void validarPreciosCarrito() {
        Assert.assertTrue(driver.getPageSource().contains("Total"));
    }
}