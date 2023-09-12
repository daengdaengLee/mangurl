package io.github.daengdaenglee.mangurl.application.url.inboundport;

public interface EncodeUrlService {
    boolean validate(String url);

    /**
     * @throws IllegalArgumentException 잘못된 url 을 입력한 경우
     */
    String encode(String url);
}
