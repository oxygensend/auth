package com.oxygensend.auth.helper;

import io.jsonwebtoken.security.Keys;
import java.util.Arrays;
import javax.crypto.SecretKey;

public class TokenHelper {

    public static SecretKey createSigningKey() {
        byte[] signingKeyBytes = new byte[64];
        Arrays.fill(signingKeyBytes, (byte) 0);
        return Keys.hmacShaKeyFor(signingKeyBytes);
    }
}
