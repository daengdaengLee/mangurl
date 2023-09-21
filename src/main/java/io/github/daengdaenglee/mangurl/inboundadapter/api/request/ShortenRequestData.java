package io.github.daengdaenglee.mangurl.inboundadapter.api.request;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequestData(@NotBlank String url) {
}
