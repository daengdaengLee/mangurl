package io.github.daengdaenglee.mangurl.inboundadapter.api.request;

public record ShortenRequest(Data data) {
    public record Data(String url) {
    }
}
