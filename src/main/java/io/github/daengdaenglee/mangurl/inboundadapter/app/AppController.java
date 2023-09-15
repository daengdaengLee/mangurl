package io.github.daengdaenglee.mangurl.inboundadapter.app;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.inboundadapter.app.form.ShortenUrlForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

@Slf4j
@Controller
@RequestMapping(AppController.basePath)
class AppController {
    static final String basePath = "/app";

    private final String origin;
    private final EncodeUrlService encodeUrlService;
    private final ShortenUrlService shortenUrlService;

    AppController(
            MangurlProperties mangurlProperties,
            EncodeUrlService encodeUrlService,
            ShortenUrlService shortenUrlService) {
        this.origin = mangurlProperties.getOrigin();
        this.encodeUrlService = encodeUrlService;
        this.shortenUrlService = shortenUrlService;
    }

    @GetMapping("/")
    String appSlash() {
        return "redirect:" + this.createFullUrl(AppController.basePath);
    }

    @GetMapping
    String app(
            @RequestParam(value = "result", required = false) String result,
            @RequestParam(value = "resOriginalUrl", defaultValue = "") String resOriginalUrl,
            @RequestParam(value = "resShortUrl", defaultValue = "") String resShortUrl,
            @RequestParam(value = "prevOriginalUrl", defaultValue = "") String prevOriginalUrl,
            Model model) {
        if (result == null) {
            model.addAttribute("isResult", false);
            model.addAttribute("prevOriginalUrl", prevOriginalUrl);
        } else {
            model.addAttribute("isResult", true);

            // @TODO encode 에러가 발생했을 때 에러 페이지 보여주기

            model.addAttribute("resOriginalUrl", resOriginalUrl);
            var resEncodedOriginalUrl = "";
            try {
                resEncodedOriginalUrl = this.encodeUrlService.encode(resOriginalUrl);
            } catch (IllegalArgumentException ignored) {
            }
            model.addAttribute("resEncodedOriginalUrl", resEncodedOriginalUrl);

            model.addAttribute("resShortUrl", resShortUrl);
            var resEncodedShortUrl = "";
            try {
                resEncodedShortUrl = this.encodeUrlService.encode(resShortUrl);
            } catch (IllegalArgumentException ignored) {
            }
            model.addAttribute("resEncodedShortUrl", resEncodedShortUrl);
        }
        return "app";
    }

    @PostMapping
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
        var shortUrl = this.createFullUrl(shortUrlCode);

        redirectAttributes.addAttribute("result", "");
        redirectAttributes.addAttribute("resOriginalUrl", originalUrl);
        redirectAttributes.addAttribute("resShortUrl", shortUrl);
        return "redirect:" + this.createFullUrl(AppController.basePath);
    }

    private String createFullUrl(String path) {
        return URI.create(this.origin).resolve(path).toASCIIString();
    }
}
