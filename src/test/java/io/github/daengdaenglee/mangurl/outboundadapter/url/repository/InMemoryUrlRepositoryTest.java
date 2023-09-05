package io.github.daengdaenglee.mangurl.outboundadapter.url.repository;

import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryUrlRepositoryTest {
    private final String testOriginalUrl1 = "https://google.com";
    private final String testOriginalUrl2 = "https://naveer.com";
    private final String testShortUrlCode1 = "abcdefg";
    private final String testShortUrlCode2 = "hijklmn";

    @Test
    @DisplayName("존재하는 originalUrl 로 조회하면 매핑된 shortUrlCode 를 담은 Optional 객체를 반환한다.")
    void findExistingShortUrlCode() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        var result = urlRepository.findShortUrlCodeByOriginalUrl(this.testOriginalUrl1);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("존재하지 않는 originalUrl 로 조회하면 빈 Optional 객체를 반환한다.")
    void findNotExistingShortUrlCode() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        var result = urlRepository.findShortUrlCodeByOriginalUrl(this.testOriginalUrl2);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 originalUrl - shortUrlCode 쌍을 저장할 수 있다.")
    void saveNotExistingEntry() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        // testOriginalUrl1, testShortUrlCode1 사용

        // when
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // then
        var originalUrlResult = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode1);
        assertThat(originalUrlResult.isPresent()).isTrue();
        assertThat(originalUrlResult.get()).isEqualTo(this.testOriginalUrl1);

        var shortUrlCodeResult = urlRepository.findShortUrlCodeByOriginalUrl(this.testOriginalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("존재하는 originalUrl - shortUrlCode 쌍을 저장하면 현재 상태를 그대로 유지한다.")
    void saveExistingEntry() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // then
        var originalUrlResult = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode1);
        assertThat(originalUrlResult.isPresent()).isTrue();
        assertThat(originalUrlResult.get()).isEqualTo(this.testOriginalUrl1);

        var shortUrlCodeResult = urlRepository.findShortUrlCodeByOriginalUrl(this.testOriginalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("다른 originalUrl 을 같은 shortUrlCode 와 저장하려고 하면 DuplicateShortUrlCodeException 예외가 발생한다.")
    void saveDifferentShortUrlCodeWithSameOriginalUrl() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when & then
        assertThatThrownBy(() -> urlRepository.save(this.testOriginalUrl2, this.testShortUrlCode1))
                .isInstanceOf(DuplicateShortUrlCodeException.class);
    }

    @Test
    @DisplayName("""
            다른 shortUrlCode 를 같은 originalUrl 과 저장할 수 있다.
            각 shortUrlCode 에 동일한 originalUrl 이 매핑되고
            originalUrl 에 처음 shortUrlCode 가 매핑된다.""")
    void saveDifferentOriginalUrlWithSameShortUrlCode() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode2);

        // then
        var originalUrlResult1 = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode1);
        assertThat(originalUrlResult1.isPresent()).isTrue();
        assertThat(originalUrlResult1.get()).isEqualTo(this.testOriginalUrl1);

        var originalUrlResult2 = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode2);
        assertThat(originalUrlResult2.isPresent()).isTrue();
        assertThat(originalUrlResult2.get()).isEqualTo(this.testOriginalUrl1);

        var shortUrlCodeResult = urlRepository.findShortUrlCodeByOriginalUrl(this.testOriginalUrl1);
        assertThat(shortUrlCodeResult.isPresent()).isTrue();
        assertThat(shortUrlCodeResult.get()).isEqualTo(this.testShortUrlCode1);
    }

    @Test
    @DisplayName("존재하는 shortUrlCode 로 조회하면 매핑된 originalUrl 를 담은 Optional 객체를 반환한다.")
    void findExistingOriginalUrl() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        var result = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode1);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testOriginalUrl1);
    }

    @Test
    @DisplayName("존재하지 않는 shortUrlCode 로 조회하면 빈 Optional 객체를 반환한다.")
    void findNotExistingOriginalUrl() {
        // given
        var urlRepository = new InMemoryUrlRepository();
        urlRepository.save(this.testOriginalUrl1, this.testShortUrlCode1);

        // when
        var result = urlRepository.findOriginalUrlByShortUrlCode(this.testShortUrlCode2);

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}