package io.github.daengdaenglee.mangurl.application.url.service;

import io.github.daengdaenglee.mangurl.TestUrlData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MangleServiceTest {
    private MangleService mangleService;
    private TestUrlData testUrlData;

    @BeforeEach
    void beforeEach() {
        var hashService = new HashService();
        var base62EncodeService = new Base62EncodeService();
        this.mangleService = new MangleService(
                hashService,
                base62EncodeService);

        this.testUrlData = new TestUrlData();
    }

    @Test
    @DisplayName("mangle 한 shortUrl 은 7글자여야 한다.")
    void length7() {
        // given
        // originalUrl1, originalUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.testUrlData.originalUrl1);
        var mangled2 = this.mangleService.mangle(this.testUrlData.originalUrl2);

        // then
        assertThat(mangled1.length()).isEqualTo(7);
        assertThat(mangled2.length()).isEqualTo(7);
    }

    @Test
    @DisplayName("mangle 한 결과는 hash -> encode -> substring -> 0 padding 한 결과와 같다.")
    void isEqual() {
        // given
        // originalUrl1, originalUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.testUrlData.originalUrl1);
        var mangled2 = this.mangleService.mangle(this.testUrlData.originalUrl2);

        // then
        assertThat(mangled1).isEqualTo(this.testUrlData.shortUrlCode1);
        assertThat(mangled2).isEqualTo(this.testUrlData.shortUrlCode2);
    }

    @Test
    @DisplayName("같은 URL 을 넣으면 같은 결과가 나온다.")
    void sameInputSameOutput() {
        // given
        // originalUrl1 사용

        // when
        var mangled1 = this.mangleService.mangle(this.testUrlData.originalUrl1);
        var mangled2 = this.mangleService.mangle(this.testUrlData.originalUrl1);

        // then
        assertThat(mangled1).isEqualTo(mangled2);
    }

    @Test
    @DisplayName("다른 URL 을 넣으면 다른 결과가 나온다. (해시 충돌 안 나는 일반적인 상황 테스트)")
    void diffInputDiffOutput() {
        // given
        // originalUrl1, originalUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.testUrlData.originalUrl1);
        var mangled2 = this.mangleService.mangle(this.testUrlData.originalUrl2);

        // then
        assertThat(mangled1).isNotEqualTo(mangled2);
    }
}