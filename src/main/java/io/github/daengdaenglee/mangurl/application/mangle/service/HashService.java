package io.github.daengdaenglee.mangurl.application.mangle.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
class HashService {
    private final MessageDigest md;

    HashService() {
        try {
            this.md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] hash(String message) {
        this.md.update(message.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    byte[] hash(String message, String salt) {
        var saltedMessage = message + salt;
        this.md.update(saltedMessage.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }
}
