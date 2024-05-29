package ar.edu.utn.frba.tacs.helpers.hash.impl;

import ar.edu.utn.frba.tacs.helpers.hash.HashingHelper;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class GuavaHashingHelper implements HashingHelper {


    @Override
    public String hash(String input) {
        return Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
    }
}
