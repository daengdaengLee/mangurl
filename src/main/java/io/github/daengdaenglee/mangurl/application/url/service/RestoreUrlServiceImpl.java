package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@RequiredArgsConstructor
@Service
class RestoreUrlServiceImpl implements RestoreUrlService {
    private final UrlRepository urlRepository;

    @Override
    public Optional<RestoredUrl> restoreUrl(String shortUrlCode) {
        return this.urlRepository.findOriginalUrlByShortUrlCode(shortUrlCode)
                .map(this::toRestoredUrl);
    }

    private RestoredUrl toRestoredUrl(String originalUrl) {
        var encodedUrl = this.encodeUrl(originalUrl);
        return new RestoredUrl(originalUrl, encodedUrl);
    }

    private String encodeUrl(String originalUrl) {
        try {
            return new URL(originalUrl).toURI().toASCIIString();
        } catch (URISyntaxException | MalformedURLException e) {
            // 저장할 때 미리 검사 -> 조회할 땐 RuntimeException 으로 처리
            throw new RuntimeException(e);
        }
    }
}
