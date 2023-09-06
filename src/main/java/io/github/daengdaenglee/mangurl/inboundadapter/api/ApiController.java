package io.github.daengdaenglee.mangurl.inboundadapter.api;

import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.inboundadapter.api.request.ShortenRequest;
import io.github.daengdaenglee.mangurl.inboundadapter.api.response.ShortenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {
    private final ShortenUrlService shortenUrlService;

    @PostMapping("/shorten")
    public ShortenResponse shorten(@RequestBody ShortenRequest shortenRequest) {
        var shortUrlCode = this.shortenUrlService.shortenUrl(shortenRequest.data().url());
        return ShortenResponse
                .builder()
                // @TODO origin 주소 설정으로 분리
                .shortUrl("http://localhost:8080/" + shortUrlCode)
                .build();
    }
}
