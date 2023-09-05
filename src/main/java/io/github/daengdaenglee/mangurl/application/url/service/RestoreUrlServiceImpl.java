package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestoreUrlServiceImpl implements RestoreUrlService {
    private final UrlRepository urlRepository;

    @Override
    public Optional<String> restoreUrl(String shortUrlCode) {
        return this.urlRepository.findOriginalUrlByShortUrlCode(shortUrlCode);
    }
}
