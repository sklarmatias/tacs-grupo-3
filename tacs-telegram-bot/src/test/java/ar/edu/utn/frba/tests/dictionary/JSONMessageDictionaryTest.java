package ar.edu.utn.frba.tests.dictionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.dictionary.JSONMessageDictionary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class JSONMessageDictionaryTest {

    private JsonNode spanishTranslations;

    private JSONMessageDictionary jsonMessageDictionary;


    @Before
    public void loadJSON() throws URISyntaxException, IOException {
        File file = new File(getClass().getResource("/messages/spanish.json").toURI());
        spanishTranslations = new ObjectMapper().readTree(file);
        jsonMessageDictionary = new JSONMessageDictionary();
    }

    @Test
    public void getHelpTranslation(){
        Assert.assertEquals(spanishTranslations.path("HELP").asText(), jsonMessageDictionary.getMessage("HELP", "spanish.json"));
    }

}
