package io.github.daengdaenglee.mangurl.application.url.inboundport;

import java.util.Optional;

public interface RestoreUrlService {
    Optional<String> restoreUrl(String shortUrlCode);
}
