package io.github.daengdaenglee.mangurl.application.url.inboundport;

import java.util.Optional;

public interface RestoreUrlService {
    Optional<RestoredUrl> restoreUrl(String shortUrlCode);

    record RestoredUrl(String originalUrl, String encodedOriginalUrl) {
    }
}
