package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.TestUrlData;
import io.github.daengdaenglee.mangurl.application.url.inboundport.ShortenUrlService;
import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortenUrlServiceImplTest {
    @Mock
    private MangleService mangleService;
    @Mock
    private UrlRepository urlRepository;
    private ShortenUrlService shortenUrlService;
    private TestUrlData testUrlData;

    @BeforeEach
    void beforeEach() {
        this.shortenUrlService = new ShortenUrlServiceImpl(this.mangleService, this.urlRepository);
        this.testUrlData = new TestUrlData();
    }

    @Test
    @DisplayName("저장되어 있는 originalUrl 을 단축하려는 경우, 매핑되어 있는 shortUrlCode 를 바로 반환한다.")
    void shortenExistingOriginalUrl() {
        // given
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.of(this.testUrlData.shortUrlCode1));

        // when
        var result = this.shortenUrlService.shortenUrl(this.testUrlData.originalUrl1);

        // then
        this.verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(1, this.testUrlData.originalUrl1);
        assertThat(result).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("재시도 도중 originalUrl 에 매핑된 shortUrlCode 를 찾은 경우, 매핑되어 있는 shortUrlCode 를 바로 반환한다.")
    void shortenExistingOriginalUrl2() {
        // given
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(this.testUrlData.shortUrlCode1));
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testUrlData.shortUrlCode2);
        doThrow(new DuplicateShortUrlCodeException())
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = this.shortenUrlService.shortenUrl(this.testUrlData.originalUrl1);

        // then
        this.verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(2, this.testUrlData.originalUrl1);
        this.verifyMangleServiceMangle(1, this.testUrlData.originalUrl1);
        this.verifyUrlRepositorySave(1, this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode2);
        assertThat(result).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("""
            저장되어 있지 않는 originalUrl 을 단축하려는 경우,
            originalUrl 을 mangle 한 shortUrlCode 를 저장할 때 해시 출돌이 발생하지 않은 경우,
            해당 shortUrlCode 를 반환한다.""")
    void shortenNotExistingOriginalUrl() {
        // given
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testUrlData.shortUrlCode1);
        doNothing()
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = this.shortenUrlService.shortenUrl(this.testUrlData.originalUrl1);

        // then
        this.verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(1, this.testUrlData.originalUrl1);
        this.verifyMangleServiceMangle(1, this.testUrlData.originalUrl1);
        this.verifyUrlRepositorySave(1, this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);
        assertThat(result).isEqualTo(this.testUrlData.shortUrlCode1);
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
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testUrlData.shortUrlCode1)
                .thenReturn(this.testUrlData.shortUrlCode2);
        doThrow(new DuplicateShortUrlCodeException())
                .doNothing()
                .when(this.urlRepository).save(anyString(), anyString());

        // when
        var result = this.shortenUrlService.shortenUrl(this.testUrlData.originalUrl1);

        // then
        this.verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(2, this.testUrlData.originalUrl1);
        this.verifyMangleServiceMangle(2, this.testUrlData.originalUrl1);
        this.verifyUrlRepositorySave(
                2,
                this.testUrlData.originalUrl1,
                this.testUrlData.shortUrlCode1,
                this.testUrlData.shortUrlCode2);
        assertThat(result).isEqualTo(this.testUrlData.shortUrlCode2);
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
        when(this.urlRepository.findShortUrlCodeByOriginalUrl(anyString()))
                .thenReturn(Optional.empty());
        when(this.mangleService.mangle(anyString()))
                .thenReturn(this.testUrlData.shortUrlCode1)
                .thenReturn(this.testUrlData.shortUrlCode2)
                .thenReturn(this.testUrlData.shortUrlCode3);
        doThrow(new DuplicateShortUrlCodeException())
                .when(this.urlRepository).save(anyString(), anyString());

        // when & then
        assertThatThrownBy(() -> this.shortenUrlService.shortenUrl(this.testUrlData.originalUrl1))
                .isInstanceOf(RuntimeException.class);

        this.verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(3, this.testUrlData.originalUrl1);
        this.verifyMangleServiceMangle(3, this.testUrlData.originalUrl1);
        this.verifyUrlRepositorySave(
                3,
                this.testUrlData.originalUrl1,
                this.testUrlData.shortUrlCode1,
                this.testUrlData.shortUrlCode2,
                this.testUrlData.shortUrlCode3);
    }

    private void verifyUrlRepositoryFindShortUrlCodeByOriginalUrl(int n, String expectedOriginalUrl) {
        var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.urlRepository, times(n))
                .findShortUrlCodeByOriginalUrl(originalUrlCaptor.capture());
        var capturedOriginalUrls = originalUrlCaptor.getAllValues();

        assertThat(capturedOriginalUrls).hasSize(n);
        capturedOriginalUrls.forEach(captured -> assertThat(captured).isEqualTo(expectedOriginalUrl));
    }

    private void verifyMangleServiceMangle(int n, String saltedOriginalUrl) {
        var messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.mangleService, times(n))
                .mangle(messageCaptor.capture());
        var capturedMessages = messageCaptor.getAllValues();

        assertThat(capturedMessages).hasSize(n);
        // 첫 번째 시도는 salt 없이 원본 URL 사용
        assertThat(capturedMessages.get(0)).isEqualTo(saltedOriginalUrl);
        capturedMessages.forEach(captured -> assertThat(captured).startsWith(saltedOriginalUrl));
        assertThat(capturedMessages).doesNotHaveDuplicates();
    }

    private void verifyUrlRepositorySave(int n, String originalUrl, String... shortUrlCodes) {
        var originalUrlCaptor = ArgumentCaptor.forClass(String.class);
        var shortUrlCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.urlRepository, times(n))
                .save(originalUrlCaptor.capture(), shortUrlCodeCaptor.capture());
        var capturedOriginalUrls = originalUrlCaptor.getAllValues();
        var capturedShortUrlCodes = shortUrlCodeCaptor.getAllValues();

        assertThat(capturedOriginalUrls).hasSize(n);
        capturedOriginalUrls.forEach(captured -> assertThat(captured).isEqualTo(originalUrl));

        assertThat(capturedShortUrlCodes).hasSize(n);
        IntStream.range(0, n).forEach(i -> {
            var captured = capturedShortUrlCodes.get(i);
            var shortUrlCode = shortUrlCodes[i];
            assertThat(captured).isEqualTo(shortUrlCode);
        });
    }
}