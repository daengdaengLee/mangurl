package io.github.daengdaenglee.mangurl.inboundadapter.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class RootController {
    @RequestMapping({"", "/"})
    String home() {
        return "redirect:" + AppController.baseUrl;
    }
}
