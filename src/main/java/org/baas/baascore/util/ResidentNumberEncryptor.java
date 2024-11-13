package org.baas.baascore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ResidentNumberEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // GCM 모드의 권장 IV 길이
    private static final int GCM_TAG_LENGTH = 128; // 인증 태그 길이
    private final byte[] secretKey;

    public ResidentNumberEncryptor(@Value("${aes.secret.key}") String key) {
        this.secretKey = key.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String data) throws GeneralSecurityException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM);

        // 무작위 IV 생성
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 암호화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // IV와 암호화된 데이터를 결합하여 Base64로 인코딩
        byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public String decrypt(String encryptedData) throws GeneralSecurityException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM);

        // Base64로 디코딩하여 IV와 암호화된 데이터를 분리
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedBytes = new byte[decodedData.length - GCM_IV_LENGTH];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);
        System.arraycopy(decodedData, iv.length, encryptedBytes, 0, encryptedBytes.length);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 복호화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
