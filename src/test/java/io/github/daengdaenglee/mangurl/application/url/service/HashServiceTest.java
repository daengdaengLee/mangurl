package io.github.daengdaenglee.mangurl.application.url.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

class HashServiceTest {
    private MessageDigest md;

    @BeforeEach
    void beforeEach() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("MD5");
    }


    @Test
    @DisplayName("입력한 메시지를 MD5 알고리즘으로 해싱한 결과를 바이트 배열로 반환한다.")
    void hash() {
        // given
        var message = "https://google.com";
        var hashService = new HashService();

        // when
        var hashed = hashService.hash(message);

        // then
        this.md.update(message.getBytes());
        var expected = this.md.digest();
        assertThat(hashed).isEqualTo(expected);
    }
}