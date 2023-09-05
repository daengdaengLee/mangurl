package io.github.daengdaenglee.mangurl.application.url.inboundport;

public interface ShortenUrlService {
    /**
     * @throws RuntimeException originalUrl 에 대한 shortUrlCode 를 해시 충돌 때문에 저장할 수 없는 경우
     */
    String shortenUrl(String originalUrl);
}
