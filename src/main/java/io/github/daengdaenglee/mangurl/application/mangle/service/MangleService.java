package io.github.daengdaenglee.mangurl.application.mangle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MangleService {
    private final HashService hashService;
    private final Base62EncodeService base62EncodeService;

    private final int len = 7;

    public String mangle(String longUrl) {
        var hashed = this.hashService.hash(longUrl);
        var encoded = this.base62EncodeService.encode(hashed);
        var sliced = this.slice(encoded);
        return this.padZero(sliced);
    }

    private String slice(String value) {
        var endIndex = Math.min(this.len, value.length());
        return value.substring(0, endIndex);
    }

    private String padZero(String value) {
        if (value.length() >= this.len) {
            return value;
        }
        var n = this.len - value.length();
        return "0".repeat(n) + value;
    }
}
