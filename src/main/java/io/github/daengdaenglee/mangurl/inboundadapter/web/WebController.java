package io.github.daengdaenglee.mangurl.inboundadapter.web;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.inboundadapter.web.form.ShortenUrlForm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class WebController {
    private final ShortenUrlService shortenUrlService;
    private final RestoreUrlService restoreUrlService;

    @RequestMapping("/{shortUrlCode}")
    public String redirect(@PathVariable String shortUrlCode, HttpServletResponse response) {
        return this.restoreUrlService.restoreUrl(shortUrlCode)
                .map(response::encodeRedirectURL)
                .map(redirectUrl -> "redirect:" + redirectUrl)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/app")
    public String app() {
        return "app";
    }

    // @TODO 임시, 나중에 app.html 에서 JS 로 API 호출하는 형태로 변경
    @PostMapping("/app")
    public String shorten(
            @ModelAttribute ShortenUrlForm shortenUrlForm,
            RedirectAttributes redirectAttributes) {
        var originalUrl = shortenUrlForm.originalUrl();
        var shortUrlCode = this.shortenUrlService.shortenUrl(originalUrl);
        var shortUrl = "http://localhost:8080/" + shortUrlCode;

        redirectAttributes.addAttribute("originalUrl", originalUrl);
        redirectAttributes.addAttribute("shortUrl", shortUrl);
        return "redirect:/app";
    }
}
