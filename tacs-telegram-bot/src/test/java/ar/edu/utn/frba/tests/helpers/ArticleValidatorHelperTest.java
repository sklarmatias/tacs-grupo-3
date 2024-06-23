package ar.edu.utn.frba.tests.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.helper.ArticleValidatorHelper;

public class ArticleValidatorHelperTest {

    @Test
    public void testValidateArticleName_empty() {
        String result = ArticleValidatorHelper.validateArticleName("");
        Assert.assertEquals("EMPTY", result);
    }

    @Test
    public void testValidateArticleName_tooLong() {
        String articleName = "ThisIsANameThatIsDefinitelyTooLongAndExceedsTheMaximumAllowedLength";
        String result = ArticleValidatorHelper.validateArticleName(articleName);
        Assert.assertEquals("ARTICLE_NAME_TOO_LONG", result);
    }

    @Test
    public void testValidateArticleName_tooShort() {
        String result = ArticleValidatorHelper.validateArticleName("Short");
        Assert.assertEquals("ARTICLE_NAME_TOO_SHORT", result);
    }

    @Test
    public void testValidateArticleName_invalidCharacters() {
        String result = ArticleValidatorHelper.validateArticleName("Invalid$Character");
        Assert.assertEquals("INVALID_CHARACTERS", result);
    }

    @Test
    public void testValidateArticleName_valid() {
        String result = ArticleValidatorHelper.validateArticleName("ValidArticleName123");
        Assert.assertNull(result);
    }

    @Test
    public void testValidateUserGets_empty() {
        String result = ArticleValidatorHelper.validateUserGets("");
        Assert.assertEquals("EMPTY", result);
    }

    @Test
    public void testValidateUserGets_invalidCharacters() {
        String result = ArticleValidatorHelper.validateUserGets("Invalid$Text");
        Assert.assertEquals("INVALID_CHARACTERS", result);
    }

    @Test
    public void testValidateUserGets_valid() {
        String result = ArticleValidatorHelper.validateUserGets("ValidText123");
        Assert.assertNull(result);
    }
}
