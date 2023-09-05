package io.github.daengdaenglee.mangurl.application.url.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Service
class MangleService {
    private final HashService hashService;
    private final Base62EncodeService base62EncodeService;
    private final int len = 7;

    String mangle(String longUrl) {
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
