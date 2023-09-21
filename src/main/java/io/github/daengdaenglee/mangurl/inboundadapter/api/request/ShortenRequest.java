package io.github.daengdaenglee.mangurl.inboundadapter.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ShortenRequest(@Valid @NotNull ShortenRequestData data) {
}
