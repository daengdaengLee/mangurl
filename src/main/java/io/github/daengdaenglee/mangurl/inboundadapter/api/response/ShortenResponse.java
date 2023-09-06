package io.github.daengdaenglee.mangurl.inboundadapter.api.response;

import lombok.Builder;

public record ShortenResponse(Data data) {
    @Builder
    private ShortenResponse(String shortUrl) {
        this(new Data(shortUrl));
    }

    private record Data(String shortUrl) {
    }
}
