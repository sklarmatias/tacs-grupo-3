
package TACS.TACS;

import jakarta.jws.WebService;

@WebService(endpointInterface = "TACS.TACS.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {
        return "Hello " + text;
    }
}

