package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
class RestoreUrlServiceImpl implements RestoreUrlService {
    private final EncodeUrlService encodeUrlService;
    private final UrlRepository urlRepository;

    @Override
    public Optional<RestoredUrl> restoreUrl(String shortUrlCode) {
        return this.urlRepository.findOriginalUrlByShortUrlCode(shortUrlCode)
                .map(this::toRestoredUrl);
    }

    private RestoredUrl toRestoredUrl(String originalUrl) {
        // 저장되어 있는 originalUrl 은 모두 valid 한 것으로 판단, 별도 예외 처리 X
        var encodedUrl = this.encodeUrlService.encode(originalUrl);
        return new RestoredUrl(originalUrl, encodedUrl);
    }
}
