package alivium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST = "blacklist:";
    private static final String VERIFICATION_CODE = "verification:";
    private static final String RESET_PASSWORD_CODE = "reset:";


    public void blacklistToken(String token, long expirationTimeInMillis) {
        String key = BLACKLIST + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        String key = BLACKLIST + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void saveVerificationCode(String email, String verificationCode) {
        String key = VERIFICATION_CODE + email;
        redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES); // ✅ 10 MINUTES
    }

    public String getVerificationCode(String email) {
        String key = VERIFICATION_CODE + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteVerificationCode(String email) {
        String key = VERIFICATION_CODE + email;
        redisTemplate.delete(key);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = getVerificationCode(email);
        return storedCode != null && storedCode.equals(code);
    }

    public void savePasswordResetCode(String email, String code) {
        String key = RESET_PASSWORD_CODE + email;
        redisTemplate.opsForValue().set(key, code, 15, TimeUnit.MINUTES);
    }

    public String getPasswordResetCode(String email) {
        String key = RESET_PASSWORD_CODE + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deletePasswordResetCode(String email) {
        String key = RESET_PASSWORD_CODE + email;
        redisTemplate.delete(key);
    }

    public boolean verifyPasswordResetCode(String email, String code) {
        String storedCode = getPasswordResetCode(email);
        return storedCode != null && storedCode.equals(code);
    }
}