package ar.edu.utn.frba.tacs;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

public class ServletInitializerIT {
    private static String endpointUrl;

    @Test
    public void testPing() throws Exception {
        WebClient client = WebClient.create("");
        Response r = client.accept("text/plain").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream)r.getEntity());
        assertEquals("SierraTangoNevada", value);
    }

    @Test
    public void testJsonRoundtrip() {
        List<Object> providers = new ArrayList<>();
        providers.add(new com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider());
        JsonBean inputBean = new JsonBean();
        inputBean.setVal1("Maple");
        WebClient client = WebClient.create(endpointUrl + "/hello/jsonBean", providers);
        Response r = client.accept("application/json")
            .type("application/json")
            .post(inputBean);
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        JsonBean output = r.readEntity(JsonBean.class);
        assertEquals("Maple", output.getVal2());
    }
}
