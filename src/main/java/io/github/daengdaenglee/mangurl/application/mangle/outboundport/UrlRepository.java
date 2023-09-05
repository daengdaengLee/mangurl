package io.github.daengdaenglee.mangurl.application.mangle.outboundport;

import java.util.Optional;

public interface UrlRepository {
    Optional<String> readShortUrlCodeByOriginalUrl(String originalUrl);

    /**
     * @throws DuplicateShortUrlCodeException 같은 shortUrlCode 에 다른 originalUrl 이 이미 매핑되어 있는 경우 발생
     */
    void save(String originalUrl, String shortUrlCode);

    Optional<String> readOriginalUrlByShortUrlCode(String shortUrlCode);
}
