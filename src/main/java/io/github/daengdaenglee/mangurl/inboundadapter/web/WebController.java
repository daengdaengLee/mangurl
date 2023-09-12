package io.github.daengdaenglee.mangurl.inboundadapter.web;

import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.inboundadapter.web.form.ShortenUrlForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
class WebController {
    private final String origin;
    private final ShortenUrlService shortenUrlService;
    private final RestoreUrlService restoreUrlService;

    WebController(
            MangurlProperties mangurlProperties,
            ShortenUrlService shortenUrlService,
            RestoreUrlService restoreUrlService) {
        this.origin = mangurlProperties.origin();
        this.shortenUrlService = shortenUrlService;
        this.restoreUrlService = restoreUrlService;
    }

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
    String app(
            @RequestParam(value = "result", required = false) String result,
            @RequestParam(value = "resOriginalUrl", defaultValue = "") String resOriginalUrl,
            @RequestParam(value = "resShortUrl", defaultValue = "") String resShortUrl,
            @RequestParam(value = "prevOriginalUrl", defaultValue = "") String prevOriginalUrl,
            Model model) {
        model.addAttribute("isResult", result != null);
        model.addAttribute("resOriginalUrl", resOriginalUrl);
        model.addAttribute("resShortUrl", resShortUrl);
        model.addAttribute("prevOriginalUrl", prevOriginalUrl);
        return "app";
    }

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
        var shortUrl = this.origin + shortUrlCode;

        redirectAttributes.addAttribute("result", "");
        redirectAttributes.addAttribute("resOriginalUrl", originalUrl);
        redirectAttributes.addAttribute("resShortUrl", shortUrl);
        return "redirect:/app";
    }
}
