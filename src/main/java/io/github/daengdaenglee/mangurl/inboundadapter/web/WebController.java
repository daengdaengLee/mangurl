package io.github.daengdaenglee.mangurl.inboundadapter.web;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Controller
public class WebController {
    private final RestoreUrlService restoreUrlService;

    @RequestMapping("/{shortUrlCode}")
    public String redirect(@PathVariable String shortUrlCode, HttpServletResponse response) {
        return this.restoreUrlService.restoreUrl(shortUrlCode)
                .map(response::encodeRedirectURL)
                .map(redirectUrl -> "redirect:" + redirectUrl)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
