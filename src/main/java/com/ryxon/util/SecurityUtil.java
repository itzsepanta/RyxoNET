package com.ryxon.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for security-related operations.
 */
public final class SecurityUtil {

    private static final String HMAC_ALGO = "HmacSHA256";

    private SecurityUtil() {}  // Prevent instantiation

    public static String generateHmac(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC generation failed", e);
        }
    }

    public static boolean verifyHmac(String data, String secret, String signature) {
        return generateHmac(data, secret).equals(signature);
    }

    public static boolean constantTimeCompare(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }

    public static String generateSessionKey() {
        return UUID.randomUUID().toString();
    }
}