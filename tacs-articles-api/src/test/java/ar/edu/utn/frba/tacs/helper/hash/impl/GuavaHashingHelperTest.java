package ar.edu.utn.frba.tacs.helper.hash.impl;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import org.junit.Assert;
import org.junit.Test;

public class GuavaHashingHelperTest {

    @Test
    public void hashStringReturnsSha256Test(){
        String baseText = "tacs2024";
        String expectedHash = "bf98518d10663411d3f389f1dd47674fe6b9d3a24a8622dde1d06583d4552a8f";
        Assert.assertEquals(expectedHash, new GuavaHashingHelper().hash(baseText));
    }

}
