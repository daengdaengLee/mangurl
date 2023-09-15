package io.github.daengdaenglee.mangurl.inboundadapter.app;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
class RootController {
    private final String origin;

    RootController(MangurlProperties mangurlProperties) {
        this.origin = mangurlProperties.getOrigin();
    }

    @RequestMapping({"", "/"})
    String home() {
        var appUrl = URI.create(this.origin).resolve(AppController.basePath).toASCIIString();
        return "redirect:" + appUrl;
    }
}
