package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortenUrlServiceImpl implements ShortenUrlService {
    private final MangleService mangleService;
    private final UrlRepository urlRepository;

    @Override
    public String shortenUrl(String originalUrl) {
        // 모든 시도가 실패한 originalUrl 을 다시 요청할 때 계속 실패하는 경우를 방지하기 위해 salt 값을 실행할 때마다 다르게 사용
        // @TODO 기본 2번 재시도, 총 3번 시도하도록 작성
        //       재시도 횟수 외부 설정으로 분리
        var salts = List.of("", UUID.randomUUID().toString(), UUID.randomUUID().toString());

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
        // 계속해서 해시 충돌이 나는 경우 application 에서 처리가 불가능하기 때문에
        // 도메인 Exception 이 아닌 RuntimeException 으로 처리
        // @TODO 예외의 메시지를 클라이언트에게 응답해도 안전한지 구분하기 위해 커스텀 예외로 바꾸고 RestControllerAdvice 로 처리할 필요 있음
        //       변경 이후 테스트도 수정해야 함
        throw new RuntimeException("단축 URL 을 생성할 수 없습니다.");
    }
}
