package io.github.daengdaenglee.mangurl.application.mangle.outboundport;

import java.util.Optional;

public interface UrlRepository {
    /**
     * 한 originalUrl 에 매핑되어 있는 shortUrlCode 는 유일하다.
     */
    Optional<String> findShortUrlCodeByOriginalUrl(String originalUrl);

    /**
     * 같은 shortUrlCode 에 다른 originalUrl 이 매핑되지 않도록 해야 한다.
     * 같은 originalUrl 에 다른 shortUrlCode 이 매핑되는 것은 허용한다.
     *
     * @throws DuplicateShortUrlCodeException 같은 shortUrlCode 에 다른 originalUrl 이 이미 매핑되어 있는 경우 발생
     */
    void save(String originalUrl, String shortUrlCode);

    /**
     * 같은 originalUrl 에 다른 shortUrlCode 가 매핑되어 있는 경우 매핑되어 있는 shortUrlCode 중 임의의 shortUrlCode 를 반환한다.
     */
    Optional<String> findOriginalUrlByShortUrlCode(String shortUrlCode);
}
