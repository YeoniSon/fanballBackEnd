package com.example.common.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Aes256Util {
    public static String alg = "AES/CBC/PKCS5Padding";
    private static final String KEY = "FANBALLKEYISFANBALLKEY"; // source key string
    private static final byte[] KEY_BYTES = java.util.Arrays.copyOf(KEY.getBytes(StandardCharsets.UTF_8), 16);
    private static final byte[] IV_BYTES = java.util.Arrays.copyOf(KEY.getBytes(StandardCharsets.UTF_8), 16);

    public static String encrypt(String text) {
        try{
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(KEY_BYTES, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_BYTES);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String cipherText) {
        try{
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(KEY_BYTES, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_BYTES);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] decodedBytes = Base64.decodeBase64(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
