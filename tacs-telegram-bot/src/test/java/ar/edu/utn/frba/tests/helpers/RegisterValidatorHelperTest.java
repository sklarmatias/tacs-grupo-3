package ar.edu.utn.frba.tests.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.helper.RegisterValidatorHelper;

public class RegisterValidatorHelperTest {

    @Test
    public void testValidateArticleName_empty() {
        String result = RegisterValidatorHelper.validateArticleName("");
        Assert.assertEquals("El nombre del artículo no puede estar vacío.", result);
    }

    @Test
    public void testValidateArticleName_tooLong() {
        String articleName = "ThisIsANameThatIsDefinitelyTooLongAndExceedsTheMaximumAllowedLength";
        String result = RegisterValidatorHelper.validateArticleName(articleName);
        Assert.assertEquals("El nombre del artículo no puede tener más de 60 caracteres.", result);
    }

    @Test
    public void testValidateArticleName_tooShort() {
        String result = RegisterValidatorHelper.validateArticleName("Short");
        Assert.assertEquals("El nombre del articulo debe tener mas de 10 caracteres", result);
    }

    @Test
    public void testValidateArticleName_invalidCharacters() {
        String result = RegisterValidatorHelper.validateArticleName("Invalid$Character");
        Assert.assertEquals("contiene caracteres no permitidos", result);
    }

    @Test
    public void testValidateArticleName_valid() {
        String result = RegisterValidatorHelper.validateArticleName("ValidArticleName123");
        Assert.assertNull(result);
    }

    @Test
    public void testValidateEmail_empty() {
        String result = RegisterValidatorHelper.validateEmail("");
        Assert.assertEquals("ERROR_EMAIL_EMPTY", result);
    }

    @Test
    public void testValidateEmail_invalid() {
        String result = RegisterValidatorHelper.validateEmail("invalid.email");
        Assert.assertEquals("ERROR_EMAIL_INVALID", result);
    }

    @Test
    public void testValidateEmail_valid() {
        String result = RegisterValidatorHelper.validateEmail("valid.email@example.com");
        Assert.assertNull(result);
    }

    @Test
    public void testValidatePassword_tooShort() {
        String result = RegisterValidatorHelper.validatePassword("Short1");
        Assert.assertEquals("ERROR_PASSWORD_INVALID", result);
    }

    @Test
    public void testValidatePassword_noDigit() {
        String result = RegisterValidatorHelper.validatePassword("NoDigits");
        Assert.assertEquals("ERROR_PASSWORD_INVALID", result);
    }

    @Test
    public void testValidatePassword_noLowerCase() {
        String result = RegisterValidatorHelper.validatePassword("NODIGITSMAYUS1");
        Assert.assertEquals("ERROR_PASSWORD_INVALID", result);
    }

    @Test
    public void testValidatePassword_valid() {
        String result = RegisterValidatorHelper.validatePassword("ValidPassword1");
        Assert.assertNull(result);
    }

    @Test
    public void testValidatePassword_edgeCase() {
        // Edge case: exactly 8 characters, includes all required types
        String result = RegisterValidatorHelper.validatePassword("P@ssw0rd");
        Assert.assertNull(result);
    }
}
