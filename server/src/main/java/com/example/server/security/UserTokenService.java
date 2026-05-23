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
public class UserTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long tokenTtlMillis;

    public UserTokenService(
            @Value("${user.auth.secret:${USER_AUTH_SECRET:}}") String secret,
            @Value("${user.auth.token-ttl-millis:${USER_AUTH_TOKEN_TTL_MILLIS:43200000}}") long tokenTtlMillis
    ) {
        this.secret = secret;
        this.tokenTtlMillis = tokenTtlMillis;
    }

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("user.auth.secret 또는 manager.auth.secret / MANAGER_AUTH_SECRET 값을 설정해야 합니다.");
        }
    }

    public IssuedToken issueToken(String uId) {
        long expiresAt = getExpiryTimestamp();
        String payload = "USER:" + uId + ":" + expiresAt;
        String encodedPayload = URL_ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return new IssuedToken(encodedPayload + "." + signature, expiresAt);
    }

    public String validateAndGetUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }

        String payload = parts[0];
        String expectedSignature = sign(payload);
        byte[] providedSignature = parts[1].getBytes(StandardCharsets.UTF_8);
        byte[] actualSignature = expectedSignature.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(providedSignature, actualSignature)) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }

        String decodedPayload = new String(URL_DECODER.decode(payload), StandardCharsets.UTF_8);
        int lastColon = decodedPayload.lastIndexOf(':');
        if (lastColon <= 0) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }
        String identity = decodedPayload.substring(0, lastColon);
        String expiresAtStr = decodedPayload.substring(lastColon + 1);
        int typeSeparator = identity.indexOf(':');
        if (typeSeparator <= 0) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }
        String tokenType = identity.substring(0, typeSeparator);
        String uId = identity.substring(typeSeparator + 1);
        if (!"USER".equals(tokenType) || uId.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }

        long expiresAt;
        try {
            expiresAt = Long.parseLong(expiresAtStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }

        if (System.currentTimeMillis() > expiresAt) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }

        return uId;
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
            throw new IllegalStateException("고객 토큰 서명 생성에 실패했습니다.", e);
        }
    }
}
