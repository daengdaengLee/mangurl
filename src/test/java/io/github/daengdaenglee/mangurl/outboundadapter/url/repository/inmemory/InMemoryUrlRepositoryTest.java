package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.inmemory;

import io.github.daengdaenglee.mangurl.TestUrlData;
import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryUrlRepositoryTest {
    private UrlRepository urlRepository;
    private TestUrlData testUrlData;

    @BeforeEach
    void beforeEach() {
        this.urlRepository = new InMemoryUrlRepository();
        this.testUrlData = new TestUrlData();
    }

    @Test
    @DisplayName("존재하는 originalUrl 로 조회하면 매핑된 shortUrlCode 를 담은 Optional 객체를 반환한다.")
    void findExistingShortUrlCode() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        var result = this.urlRepository.findShortUrlCodeByOriginalUrl(this.testUrlData.originalUrl1);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("존재하지 않는 originalUrl 로 조회하면 빈 Optional 객체를 반환한다.")
    void findNotExistingShortUrlCode() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        var result = this.urlRepository.findShortUrlCodeByOriginalUrl(this.testUrlData.originalUrl2);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 originalUrl - shortUrlCode 쌍을 저장할 수 있다.")
    void saveNotExistingEntry() {
        // given
        // originalUrl1, shortUrlCode1 사용

        // when
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // then
        var originalUrlResult = this.urlRepository
                .findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);
        assertThat(originalUrlResult.isPresent()).isTrue();
        assertThat(originalUrlResult.get()).isEqualTo(this.testUrlData.originalUrl1);

        var shortUrlCodeResult = this.urlRepository
                .findShortUrlCodeByOriginalUrl(this.testUrlData.originalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("존재하는 originalUrl - shortUrlCode 쌍을 저장하면 현재 상태를 그대로 유지한다.")
    void saveExistingEntry() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // then
        var originalUrlResult = this.urlRepository
                .findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);
        assertThat(originalUrlResult.isPresent()).isTrue();
        assertThat(originalUrlResult.get()).isEqualTo(this.testUrlData.originalUrl1);

        var shortUrlCodeResult = this.urlRepository
                .findShortUrlCodeByOriginalUrl(this.testUrlData.originalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("다른 originalUrl 을 같은 shortUrlCode 와 저장하려고 하면 DuplicateShortUrlCodeException 예외가 발생한다.")
    void saveDifferentShortUrlCodeWithSameOriginalUrl() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when & then
        assertThatThrownBy(() -> this.urlRepository.save(
                this.testUrlData.originalUrl2, this.testUrlData.shortUrlCode1))
                .isInstanceOf(DuplicateShortUrlCodeException.class);
    }

    @Test
    @DisplayName("""
            다른 shortUrlCode 를 같은 originalUrl 과 저장할 수 있다.
            각 shortUrlCode 에 동일한 originalUrl 이 매핑되고
            originalUrl 에 처음 shortUrlCode 가 매핑된다.""")
    void saveDifferentOriginalUrlWithSameShortUrlCode() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode2);

        // then
        var originalUrlResult1 = this.urlRepository
                .findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);
        assertThat(originalUrlResult1.isPresent()).isTrue();
        assertThat(originalUrlResult1.get()).isEqualTo(this.testUrlData.originalUrl1);

        var originalUrlResult2 = this.urlRepository
                .findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode2);
        assertThat(originalUrlResult2.isPresent()).isTrue();
        assertThat(originalUrlResult2.get()).isEqualTo(this.testUrlData.originalUrl1);

        var shortUrlCodeResult = this.urlRepository
                .findShortUrlCodeByOriginalUrl(this.testUrlData.originalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testUrlData.shortUrlCode1);
    }

    @Test
    @DisplayName("존재하는 shortUrlCode 로 조회하면 매핑된 originalUrl 를 담은 Optional 객체를 반환한다.")
    void findExistingOriginalUrl() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        var result = this.urlRepository.findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode1);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testUrlData.originalUrl1);
    }

    @Test
    @DisplayName("존재하지 않는 shortUrlCode 로 조회하면 빈 Optional 객체를 반환한다.")
    void findNotExistingOriginalUrl() {
        // given
        this.urlRepository.save(this.testUrlData.originalUrl1, this.testUrlData.shortUrlCode1);

        // when
        var result = this.urlRepository.findOriginalUrlByShortUrlCode(this.testUrlData.shortUrlCode2);

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}