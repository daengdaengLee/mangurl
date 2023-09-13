package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

// @TODO 단위 테스트 작성
@Service
class EncodeUrlServiceImpl implements EncodeUrlService {
    @Override
    public boolean isValid(String url) {
        try {
            var ignored = new URI(url).toURL();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }

    @Override
    public String encode(String url) {
        try {
            var uri = new URI(url);
            var ignored = uri.toURL();
            return uri.toASCIIString();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
