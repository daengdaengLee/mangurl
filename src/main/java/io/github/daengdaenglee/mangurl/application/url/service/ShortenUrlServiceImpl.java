package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
class ShortenUrlServiceImpl implements ShortenUrlService {
    private final EncodeUrlService encodeUrlService;
    private final MangleService mangleService;
    private final UrlRepository urlRepository;

    @Override
    public String shortenUrl(String originalUrl) {
        this.validateUrl(originalUrl);
        var salts = this.createSalts();
        for (var salt : salts) {
            var shortUrlCodeResult = this.urlRepository.findShortUrlCodeByOriginalUrl(originalUrl);
            if (shortUrlCodeResult.isPresent()) {
                return shortUrlCodeResult.get();
            }

            var shortUrlCode = this.mangleService.mangle(originalUrl + salt);
            try {
                this.urlRepository.save(originalUrl, shortUrlCode);
            } catch (DuplicateShortUrlCodeException e) {
                continue;
            }
            return shortUrlCode;
        }

        log.error("해시 충돌로 단축 URL 코드 생성 실패");
        throw new HashCollisionException(originalUrl);
    }

    /**
     * @throws IllegalUrlException 잘못된 url 인 경우
     */
    private void validateUrl(String url) {
        if (this.encodeUrlService.validate(url)) {
            return;
        }
        throw new IllegalUrlException(url);
    }

    private Collection<String> createSalts() {
        // 모든 시도가 실패한 originalUrl 을 다시 요청할 때 계속 실패하는 경우를 방지하기 위해 salt 값을 실행할 때마다 다르게 사용
        // @TODO 기본 2번 재시도, 총 3번 시도하도록 작성
        //       재시도 횟수 외부 설정으로 분리
        return List.of("", UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
}
