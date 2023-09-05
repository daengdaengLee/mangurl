package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortenUrlServiceImpl implements ShortenUrlService {
    private final MangleService mangleService;
    private final UrlRepository urlRepository;
    // @TODO outbound port 에서 조회하도록 수정
    private final List<String> salts = List.of("", "1", "2");

    @Override
    public String shortenUrl(String originalUrl) {
        for (var salt : this.salts) {
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
        // 계속해서 해시 충돌이 나는 경우 application 에서 처리가 불가능하기 때문에
        // 도메인 Exception 이 아닌 RuntimeException 으로 처리
        throw new RuntimeException("단축 URL 을 생성할 수 없습니다.");
    }
}
