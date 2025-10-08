package glue;

import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductLoginStepsDef {

    private WebDriver driver;
    private WebDriverWait wait;

    private double precioUnitario;
    private double totalPopup;
    private double totalCarrito;

    @Given("estoy en la página de la tienda")
    public void abrirTienda() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://qalab.bensg.com/store/en/login?back=my-account");
    }

    @And("me logueo con mi usuario {string} y clave {string}")
    public void meLogueo(String usuario, String clave) {
        try {
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("field-email")));
            WebElement passField = driver.findElement(By.id("field-password"));
            WebElement btnSubmit = driver.findElement(By.id("submit-login"));

            emailField.clear();
            emailField.sendKeys(usuario);
            passField.clear();
            passField.sendKeys(clave);
            btnSubmit.click();

            // Verificamos que el login fue exitoso (usuario logueado)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.account")));
            System.out.println("✅ Login exitoso con usuario: " + usuario);

        } catch (Exception e) {
            Assert.fail("❌ Error al intentar loguearse: " + e.getMessage());
        }
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navegarCategoria(String categoria, String subcategoria) {
        String baseUrl = "https://qalab.bensg.com/store/en/";
        try {
            if (categoria.equalsIgnoreCase("Clothes") && subcategoria.equalsIgnoreCase("Men")) {
                driver.get(baseUrl + "4-men");
            } else if (categoria.equalsIgnoreCase("Clothes")) {
                driver.get(baseUrl + "3-clothes");
            } else {
                Assert.fail("Categoría no soportada: " + categoria + " / " + subcategoria);
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("button[data-button-action='add-to-cart']")));
            System.out.println("✅ Navegación correcta a la categoría: " + categoria + " / " + subcategoria);

        } catch (Exception e) {
            Assert.fail("❌ Categoría o subcategoría no encontrada o no clickeable: " + categoria + " / " + subcategoria);
        }
    }

    @And("agrego {int} unidades del primer producto al carrito")
    public void agregarProducto(int cantidad) {
        try {
            // Primer producto visible
            WebElement primerProducto = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("article.product-miniature")));
            precioUnitario = Double.parseDouble(
                    primerProducto.findElement(By.cssSelector("span.price")).getText().replace("$", "").trim());

            WebElement botonAgregar = primerProducto.findElement(By.cssSelector("button.add-to-cart"));
            botonAgregar.click();

            // Esperar popup de confirmación
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));

            System.out.println("✅ Producto agregado al carrito. Precio unitario: $" + precioUnitario);

        } catch (Exception e) {
            Assert.fail("❌ Error al agregar producto al carrito: " + e.getMessage());
        }
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validarPopup() {
        try {
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));
            WebElement mensaje = popup.findElement(By.cssSelector("h4.modal-title"));

            Assert.assertTrue("No se muestra mensaje de confirmación",
                    mensaje.getText().contains("Product successfully added"));
            System.out.println("✅ Popup de confirmación mostrado correctamente.");

        } catch (Exception e) {
            Assert.fail("❌ No se pudo validar el popup: " + e.getMessage());
        }
    }

    @And("valido en el popup que el monto total sea calculado correctamente")
    public void validarMonto() {
        try {
            WebElement totalPopupElem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".cart-content .value")));
            totalPopup = Double.parseDouble(totalPopupElem.getText().replace("$", "").trim());

            double esperado = precioUnitario; // ya que solo agregamos 1 producto
            Assert.assertEquals("Monto total incorrecto en popup", esperado, totalPopup, 0.01);

            System.out.println("✅ Validación de monto en popup correcta. Total: $" + totalPopup);

            // Cerrar el popup
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.continue.btn.btn-secondary")));
            botonContinuar.click();

        } catch (Exception e) {
            Assert.fail("❌ Error validando monto en popup: " + e.getMessage());
        }
    }

    @When("finalizo la compra")
    public void finalizarCompra() {
        try {
            WebElement botonCarrito = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[title='View my shopping cart']")));
            botonCarrito.click();

            wait.until(ExpectedConditions.titleContains("Cart"));
            System.out.println("✅ Navegación al carrito exitosa.");

        } catch (Exception e) {
            Assert.fail("❌ Error al intentar ir al carrito: " + e.getMessage());
        }
    }

    @Then("valido el titulo de la pagina del carrito")
    public void validarTituloCarrito() {
        String titulo = driver.getTitle();
        Assert.assertTrue("El título no corresponde al carrito", titulo.toLowerCase().contains("cart"));
        System.out.println("✅ Validación del título del carrito: " + titulo);
    }

    @And("vuelvo a validar el calculo de precios en el carrito")
    public void validarPreciosCarrito() {
        try {
            WebElement totalElem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#cart-subtotal-products span.value")));
            totalCarrito = Double.parseDouble(totalElem.getText().replace("$", "").trim());

            Assert.assertEquals("Total del carrito no coincide con el popup",
                    totalPopup, totalCarrito, 0.01);
            System.out.println("✅ Validación final de precios correcta. Total carrito: $" + totalCarrito);

        } catch (Exception e) {
            Assert.fail("❌ Error validando precios en el carrito: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}