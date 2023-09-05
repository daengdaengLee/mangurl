package io.github.daengdaenglee.mangurl.outboundadapter.url.repository;

import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUrlRepository implements UrlRepository {
    private final ConcurrentHashMap<String, String> originalUrlByShortUrlCode = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> shortUrlCodeByOriginalUrl = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findShortUrlCodeByOriginalUrl(String originalUrl) {
        return Optional.ofNullable(this.shortUrlCodeByOriginalUrl.get(originalUrl));
    }

    @Override
    public void save(String originalUrl, String shortUrlCode) {
        this.originalUrlByShortUrlCode.compute(shortUrlCode, (key, val) -> {
            if (val != null && !val.equals(originalUrl)) {
                throw new DuplicateShortUrlCodeException();
            }
            return originalUrl;
        });
        // originalUrl 에 여러 shortUrlCode 가 매핑되어 있어도
        // 어떤 shortUrlCode 를 쓰든 다시 originalUrl 로 매핑할 수 있기 때문에
        // 최초에 매핑된 것 하나만 유지해도 문제 없음
        this.shortUrlCodeByOriginalUrl.putIfAbsent(originalUrl, shortUrlCode);
    }

    @Override
    public Optional<String> findOriginalUrlByShortUrlCode(String shortUrlCode) {
        return Optional.ofNullable(this.originalUrlByShortUrlCode.get(shortUrlCode));
    }
}
