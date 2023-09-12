package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.TestUrlData;
import io.github.daengdaenglee.mangurl.application.url.inboundport.RestoreUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestoreUrlServiceImplTest {
    @Mock
    private UrlRepository urlRepository;
    private RestoreUrlService restoreUrlService;
    private TestUrlData testUrlData;

    @BeforeEach
    void beforeEach() {
        this.restoreUrlService = new RestoreUrlServiceImpl(this.urlRepository);
        this.testUrlData = new TestUrlData();
    }

    @Test
    @DisplayName("존재하는 shortUrlCode 로 조회하면 매칭되는 originalUrl 을 담은 Optional<RestoredUrl> 객체를 반환한다.")
    void restoreExistingUrl() {
        // given
        when(this.urlRepository.findOriginalUrlByShortUrlCode(anyString()))
                .thenReturn(Optional.of(this.testUrlData.originalUrl1));

        // when
        var result = this.restoreUrlService.restoreUrl(this.testUrlData.shortUrlCode1);

        // then
        this.verifyUrlRepositoryFindOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get())
                .isEqualTo(new RestoreUrlService.RestoredUrl(
                        this.testUrlData.originalUrl1,
                        this.testUrlData.originalUrl1));
    }

    @Test
    @DisplayName("존재하지 않는 shortUrlCode 로 조회하면 빈 Optional 객체를 반환한다.")
    void restoreNotExistingUrl() {
        // given
        when(this.urlRepository.findOriginalUrlByShortUrlCode(anyString()))
                .thenReturn(Optional.empty());

        // when
        var result = this.restoreUrlService.restoreUrl(this.testUrlData.shortUrlCode1);

        // then
        this.verifyUrlRepositoryFindOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);
        assertThat(result.isEmpty()).isTrue();
    }

    private void verifyUrlRepositoryFindOriginalUrlByShortUrlCode(String shortUrlCode) {
        var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.urlRepository, times(1))
                .findOriginalUrlByShortUrlCode(shortUrlCodeCaptor.capture());
        var capturedShortUrlCode = shortUrlCodeCaptor.getValue();
        assertThat(capturedShortUrlCode).isEqualTo(shortUrlCode);
    }
}