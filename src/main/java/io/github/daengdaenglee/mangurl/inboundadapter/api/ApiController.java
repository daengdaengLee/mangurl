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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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
        var url = shortenRequest.data().url();
        try {
            new URL(url).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 URL 입니다.");
        }
        var shortUrlCode = this.shortenUrlService.shortenUrl(url);
        return ShortenResponse
                .builder()
                .shortUrl(this.origin + shortUrlCode)
                .build();
    }
}
