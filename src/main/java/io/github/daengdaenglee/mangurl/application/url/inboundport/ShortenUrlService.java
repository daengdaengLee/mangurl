package io.github.daengdaenglee.mangurl.application.url.inboundport;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface ShortenUrlService {
    /**
     * @throws HashCollisionException originalUrl 에 대한 shortUrlCode 를 해시 충돌 때문에 저장할 수 없는 경우
     * @throws IllegalUrlException    originalUrl 을 URL 또는 URI 객체로 변환할 수 없는 경우
     */
    String shortenUrl(String originalUrl);

    @Getter
    @RequiredArgsConstructor
    class HashCollisionException extends IllegalStateException {
        private final String url;
    }

    @Getter
    @RequiredArgsConstructor
    class IllegalUrlException extends IllegalArgumentException {
        private final String url;
    }
}
