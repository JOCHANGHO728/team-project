package com.example.server.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class ManagerTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long tokenTtlMillis;

    public ManagerTokenService(
            @Value("${manager.auth.secret:${MANAGER_AUTH_SECRET:}}") String secret,
            @Value("${manager.auth.token-ttl-millis:${MANAGER_AUTH_TOKEN_TTL_MILLIS:43200000}}") long tokenTtlMillis
    ) {
        this.secret = secret;
        this.tokenTtlMillis = tokenTtlMillis;
    }

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("manager.auth.secret 또는 MANAGER_AUTH_SECRET 값을 설정해야 합니다.");
        }
    }

    public IssuedToken issueToken(String managerId) {
        long expiresAt = getExpiryTimestamp();
        String payload = managerId + ":" + expiresAt;
        String encodedPayload = URL_ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return new IssuedToken(encodedPayload + "." + signature, expiresAt);
    }

    public String validateAndGetManagerId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("토큰 형식이 올바르지 않습니다.");
        }

        String payload = parts[0];
        String expectedSignature = sign(payload);
        byte[] providedSignature = parts[1].getBytes(StandardCharsets.UTF_8);
        byte[] actualSignature = expectedSignature.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(providedSignature, actualSignature)) {
            throw new IllegalArgumentException("토큰 서명이 유효하지 않습니다.");
        }

        String decodedPayload = new String(URL_DECODER.decode(payload), StandardCharsets.UTF_8);
        String[] payloadParts = decodedPayload.split(":");
        if (payloadParts.length != 2) {
            throw new IllegalArgumentException("토큰 payload 형식이 올바르지 않습니다.");
        }

        long expiresAt;
        try {
            expiresAt = Long.parseLong(payloadParts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("토큰 만료 시간이 올바르지 않습니다.");
        }

        if (System.currentTimeMillis() > expiresAt) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        return payloadParts[0];
    }

    public long getExpiryTimestamp() {
        return System.currentTimeMillis() + tokenTtlMillis;
    }

    public record IssuedToken(String accessToken, long expiresAt) {
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] signatureBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return URL_ENCODER.encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new IllegalStateException("관리자 토큰 서명 생성에 실패했습니다.", e);
        }
    }
}
