package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortenUrlServiceImplTest {
    private final String testOriginalUrl1 = "https://google.com";
    private final String testShortUrlCode1 = "abcdefg";
    private final String testShortUrlCode2 = "hijklmn";
    private final String testShortUrlCode3 = "opqrstu";

    @Mock
    private MangleService mangleService;
    @Mock
    private UrlRepository urlRepository;

    @Test
    @DisplayName("저장되어 있는 originalUrl 을 단축하려는 경우, 매핑되어 있는 shortUrlCode 를 바로 반환한다.")
    void shortenExistingOriginalUrl() {
        // given
        var shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.of(this.testShortUrlCode1));

        // when
        var result = shortenUrlService.shortenUrl(this.testOriginalUrl1);

        // then
        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(1))
                    .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
            var capturedOriginalUrl = originalUrlCaptor.getValue();
            assertThat(capturedOriginalUrl).isEqualTo(this.testOriginalUrl1);
        }

        assertThat(result).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("재시도 도중 originalUrl 에 매핑된 shortUrlCode 를 찾은 경우, 매핑되어 있는 shortUrlCode 를 바로 반환한다.")
    void shortenExistingOriginalUrl2() {
        // given
        var shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(this.testShortUrlCode1));
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testShortUrlCode2);
        doThrow(new DuplicateShortUrlCodeException())
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = shortenUrlService.shortenUrl(this.testOriginalUrl1);

        // then
        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(2))
                    .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
            var capturedOriginalUrls = originalUrlCaptor.getAllValues();
            assertThat(capturedOriginalUrls.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(1)).startsWith(this.testOriginalUrl1);
        }

        {
            var messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.mangleService, times(1))
                    .mangle(messageCaptor.capture());
            var capturedMessage = messageCaptor.getValue();
            assertThat(capturedMessage).isEqualTo(this.testOriginalUrl1);
        }

        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(1))
                    .save(originalUrlCaptor.capture(), shortUrlCodeCaptor.capture());
            var capturedOriginalUrl = originalUrlCaptor.getValue();
            var capturedShortUrlCode = shortUrlCodeCaptor.getValue();
            assertThat(capturedOriginalUrl).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedShortUrlCode).isEqualTo(this.testShortUrlCode2);
        }

        assertThat(result).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("""
            저장되어 있지 않는 originalUrl 을 단축하려는 경우,
            originalUrl 을 mangle 한 shortUrlCode 를 저장할 때 해시 출돌이 발생하지 않은 경우,
            해당 shortUrlCode 를 반환한다.""")
    void shortenNotExistingOriginalUrl() {
        // given
        var shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testShortUrlCode1);
        doNothing()
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = shortenUrlService.shortenUrl(this.testOriginalUrl1);

        // then
        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(1))
                    .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
            var capturedOriginalUrl = originalUrlCaptor.getValue();
            assertThat(capturedOriginalUrl).isEqualTo(this.testOriginalUrl1);
        }

        {
            var messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.mangleService, times(1))
                    .mangle(messageCaptor.capture());
            var capturedMessage = messageCaptor.getValue();
            assertThat(capturedMessage).isEqualTo(this.testOriginalUrl1);
        }

        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(1))
                    .save(originalUrlCaptor.capture(), shortUrlCodeCaptor.capture());
            var capturedOriginalUrl = originalUrlCaptor.getValue();
            var capturedShortUrlCode = shortUrlCodeCaptor.getValue();
            assertThat(capturedOriginalUrl).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedShortUrlCode).isEqualTo(this.testShortUrlCode1);
        }

        assertThat(result).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("""
            저장되어 있지 않는 originalUrl 을 단축하려는 경우,
            originalUrl 을 mangle 한 shortUrlCode 를 저장할 때 해시 출돌이 발생하면,
            랜덤 salt 를 덧붙인 originalUrl 을 mangle 한 shortUrlCode 로 다시 시도하고,
            해시 충돌이 발생하지 않은 경우,
            해당 shortUrlCode 를 반환한다.""")
    void shortenNotExistingOriginalUrl2() {
        // given
        var shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testShortUrlCode1)
                .thenReturn(this.testShortUrlCode2);
        doThrow(new DuplicateShortUrlCodeException())
                .doNothing()
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = shortenUrlService.shortenUrl(this.testOriginalUrl1);

        // then
        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(2))
                    .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
            var capturedOriginalUrls = originalUrlCaptor.getAllValues();
            assertThat(capturedOriginalUrls.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(1)).startsWith(this.testOriginalUrl1);
        }

        {
            var messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.mangleService, times(2))
                    .mangle(messageCaptor.capture());
            var capturedMessages = messageCaptor.getAllValues();
            assertThat(capturedMessages.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedMessages.get(1)).startsWith(this.testOriginalUrl1);
            assertThat(capturedMessages.get(0)).isNotEqualTo(capturedMessages.get(1));
        }

        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(2))
                    .save(originalUrlCaptor.capture(), shortUrlCodeCaptor.capture());
            var capturedOriginalUrls = originalUrlCaptor.getAllValues();
            var capturedShortUrlCodes = shortUrlCodeCaptor.getAllValues();
            assertThat(capturedOriginalUrls.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(1)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedShortUrlCodes.get(0)).isEqualTo(this.testShortUrlCode1);
            assertThat(capturedShortUrlCodes.get(1)).isEqualTo(this.testShortUrlCode2);
        }

        assertThat(result).isEqualTo(this.testShortUrlCode2);
    }

    @Test
    @DisplayName("""
            저장되어 있지 않는 originalUrl 을 단축하려는 경우,
            originalUrl 을 mangle 한 shortUrlCode 를 저장할 때 해시 출돌이 발생하면,
            랜덤 salt 를 덧붙인 originalUrl 을 mangle 한 shortUrlCode 로 다시 시도하고,
            모든 재시도에서 해시 충돌이 발생한 경우,
            RuntimeException 을 발생시킨다.""")
    void shortenNotExistingOriginalUrl3() {
        // given
        var shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testShortUrlCode1)
                .thenReturn(this.testShortUrlCode2)
                .thenReturn(this.testShortUrlCode3);
        doThrow(new DuplicateShortUrlCodeException())
                .when(this.urlRepository).save(anyString(), anyString());

        // when & then
        assertThatThrownBy(() -> shortenUrlService.shortenUrl(this.testOriginalUrl1))
                .isInstanceOf(RuntimeException.class);

        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(3))
                    .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
            var capturedOriginalUrls = originalUrlCaptor.getAllValues();
            assertThat(capturedOriginalUrls.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(1)).startsWith(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(2)).startsWith(this.testOriginalUrl1);
        }

        {
            var messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.mangleService, times(3))
                    .mangle(messageCaptor.capture());
            var capturedMessages = messageCaptor.getAllValues();
            assertThat(capturedMessages.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedMessages.get(1)).startsWith(this.testOriginalUrl1);
            assertThat(capturedMessages.get(2)).startsWith(this.testOriginalUrl1);
            assertThat(capturedMessages.get(0)).isNotEqualTo(capturedMessages.get(1));
            assertThat(capturedMessages.get(0)).isNotEqualTo(capturedMessages.get(2));
            assertThat(capturedMessages.get(1)).isNotEqualTo(capturedMessages.get(2));
        }

        {
            var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
            var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
            verify(this.urlRepository, times(3))
                    .save(originalUrlCaptor.capture(), shortUrlCodeCaptor.capture());
            var capturedOriginalUrls = originalUrlCaptor.getAllValues();
            var capturedShortUrlCodes = shortUrlCodeCaptor.getAllValues();
            assertThat(capturedOriginalUrls.get(0)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(1)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedOriginalUrls.get(2)).isEqualTo(this.testOriginalUrl1);
            assertThat(capturedShortUrlCodes.get(0)).isEqualTo(this.testShortUrlCode1);
            assertThat(capturedShortUrlCodes.get(1)).isEqualTo(this.testShortUrlCode2);
            assertThat(capturedShortUrlCodes.get(2)).isEqualTo(this.testShortUrlCode3);
        }
    }
}