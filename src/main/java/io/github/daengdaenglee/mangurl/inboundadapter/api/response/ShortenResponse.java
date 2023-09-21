package io.github.daengdaenglee.mangurl.inboundadapter.api.response;

import lombok.Builder;

public record ShortenResponse(ShortenResponseData data) {
    @Builder
    private ShortenResponse(String shortUrl) {
        this(new ShortenResponseData(shortUrl));
    }
}
