package com.gradmate.gradmate_adminsystem_springboot.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${admin.jwt.secret}")
    private String secret;

    @Value("${admin.jwt.expire-minutes}")
    private long expireMinutes;

    public String createToken(Long adminId, String username, String role) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("adminId", adminId);
            payload.put("username", username);
            payload.put("role", role);
            payload.put("exp", Instant.now().plusSeconds(expireMinutes * 60).getEpochSecond());
            String h = encode(MAPPER.writeValueAsBytes(header));
            String p = encode(MAPPER.writeValueAsBytes(payload));
            return h + "." + p + "." + sign(h + "." + p);
        } catch (Exception e) {
            throw new ApiException("生成登录凭证失败");
        }
    }

    public Map<String, Object> parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
                throw new ApiException("登录凭证无效");
            }
            Map<String, Object> payload = MAPPER.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {});
            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                throw new ApiException("登录已过期");
            }
            return payload;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("登录凭证无效");
        }
    }

    public long getExpireMinutes() {
        return expireMinutes;
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sign(String input) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return encode(mac.doFinal(input.getBytes(StandardCharsets.UTF_8)));
    }
}
