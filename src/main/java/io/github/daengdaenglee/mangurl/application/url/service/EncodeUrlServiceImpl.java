package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.inboundport.EncodeUrlService;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

// @TODO 단위 테스트 작성
@Service
class EncodeUrlServiceImpl implements EncodeUrlService {
    @Override
    public boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }

    @Override
    public String encode(String url) {
        try {
            return new URL(url).toURI().toASCIIString();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
