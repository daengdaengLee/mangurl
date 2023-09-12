package io.github.daengdaenglee.mangurl.inboundadapter.api;

import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.inboundadapter.api.request.ShortenRequest;
import io.github.daengdaenglee.mangurl.inboundadapter.api.response.ShortenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
class ApiController {
    private final String origin;
    private final ShortenUrlService shortenUrlService;

    ApiController(MangurlProperties mangurlProperties, ShortenUrlService shortenUrlService) {
        this.origin = mangurlProperties.origin();
        this.shortenUrlService = shortenUrlService;
    }

    @PostMapping("/shorten")
    ShortenResponse shorten(@RequestBody ShortenRequest shortenRequest) {
        String shortUrlCode;
        try {
            shortUrlCode = this.shortenUrlService.shortenUrl(shortenRequest.data().url());
        } catch (ShortenUrlService.IllegalUrlException e) {
            // @TODO RestControllerAdvice 에서 처리
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 URL 입니다.");
        }
        return ShortenResponse
                .builder()
                .shortUrl(this.origin + shortUrlCode)
                .build();
    }
}
