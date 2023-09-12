package io.github.daengdaenglee.mangurl.inboundadapter.web;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.inboundadapter.web.form.ShortenUrlForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
class WebController {
    private final ShortenUrlService shortenUrlService;
    private final RestoreUrlService restoreUrlService;

    @RequestMapping("/{shortUrlCode}")
    String redirect(@PathVariable String shortUrlCode) {
        return this.restoreUrlService.restoreUrl(shortUrlCode)
                .map(RestoreUrlService.RestoredUrl::encodedOriginalUrl)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping
    String home() {
        return "redirect:/app";
    }

    @GetMapping("/app")
    String app() {
        return "app";
    }

    // @TODO 임시, 나중에 app.html 에서 JS 로 API 호출하는 형태로 변경
    @PostMapping("/app")
    String shorten(
            @ModelAttribute ShortenUrlForm shortenUrlForm,
            RedirectAttributes redirectAttributes) {
        var originalUrl = shortenUrlForm.originalUrl();
        String shortUrlCode;
        try {
            shortUrlCode = this.shortenUrlService.shortenUrl(originalUrl);
        } catch (ShortenUrlService.IllegalUrlException e) {
            // @TODO binding result 를 이용하여 오류 메세지 보여주기
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 URL 입니다.");
        }
        var shortUrl = "http://localhost:8080/" + shortUrlCode;

        redirectAttributes.addAttribute("result", "");
        redirectAttributes.addAttribute("resOriginalUrl", originalUrl);
        redirectAttributes.addAttribute("resShortUrl", shortUrl);
        return "redirect:/app";
    }
}
