package io.github.daengdaenglee.mangurl.inboundadapter.shorturl;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Controller
class ShortUrlController {
    private final RestoreUrlService restoreUrlService;
    private final EncodeUrlService encodeUrlService;

    @RequestMapping({"/{shortUrlCode}", "/{shortUrlCode}/"})
    String redirect(@PathVariable String shortUrlCode) {
        return this.restoreUrlService.restoreUrl(shortUrlCode)
                .map(this.encodeUrlService::encode)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
