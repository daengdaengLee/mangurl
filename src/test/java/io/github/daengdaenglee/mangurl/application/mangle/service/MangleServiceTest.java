package io.github.daengdaenglee.mangurl.application.mangle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MangleServiceTest {
    private MangleService mangleService;

    private String longUrl1;
    private String shortUrlCode1;
    private String longUrl2;
    private String shortUrlCode2;

    @BeforeEach
    void beforeEach() {
        var hashService = new HashService();
        var base62EncodeService = new Base62EncodeService();
        this.mangleService = new MangleService(
                hashService,
                base62EncodeService);

        this.longUrl1 = "https://google.com";
        this.longUrl2 = "https://github.com";

        // 미리 hash -> encode -> substring 한 테스트 데이터 준비
        this.shortUrlCode1 = "Ep0B3mS";
        this.shortUrlCode2 = "BdrFjrW";
    }

    @Test
    @DisplayName("mangle 한 shortUrl 은 7글자여야 한다.")
    void length7() {
        // given
        // longUrl1, longUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.longUrl1);
        var mangled2 = this.mangleService.mangle(this.longUrl2);

        // then
        assertThat(mangled1.length()).isEqualTo(7);
        assertThat(mangled2.length()).isEqualTo(7);
    }

    @Test
    @DisplayName("mangle 한 결과는 hash -> encode -> substring -> 0 padding 한 결과와 같다.")
    void isEqual() {
        // given
        // longUrl1, longUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.longUrl1);
        var mangled2 = this.mangleService.mangle(this.longUrl2);

        // then
        assertThat(mangled1).isEqualTo(this.shortUrlCode1);
        assertThat(mangled2).isEqualTo(this.shortUrlCode2);
    }

    @Test
    @DisplayName("같은 URL 을 넣으면 같은 결과가 나온다.")
    void sameInputSameOutput() {
        // given
        // longUrl1 사용

        // when
        var mangled1 = this.mangleService.mangle(this.longUrl1);
        var mangled2 = this.mangleService.mangle(this.longUrl1);

        // then
        assertThat(mangled1).isEqualTo(mangled2);
    }

    @Test
    @DisplayName("다른 URL 을 넣으면 다른 결과가 나온다. (해시 충돌 안 나는 일반적인 상황 테스트)")
    void diffInputDiffOutput() {
        // given
        // longUrl1, longUrl2 사용

        // when
        var mangled1 = this.mangleService.mangle(this.longUrl1);
        var mangled2 = this.mangleService.mangle(this.longUrl2);

        // then
        assertThat(mangled1).isNotEqualTo(mangled2);
    }
}