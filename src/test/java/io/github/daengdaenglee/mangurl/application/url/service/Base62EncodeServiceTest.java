package io.github.daengdaenglee.mangurl.application.url.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncodeServiceTest {
    @Test
    @DisplayName("빈 byte 배열을 입력하면 빈 문자열을 반환한다.")
    void encodeBase62Empty() {
        // given
        var bytes = new byte[]{};
        var baseXEncodeService = new Base62EncodeService();

        // when
        var encoded = baseXEncodeService.encode(bytes);

        // then
        assertThat(encoded).isEmpty();
    }

    @Test
    @DisplayName("입력한 byte 배열을 62진법으로 변환한다.")
    void encodeBase62() {
        // given
        var expected = "aB3";
        var bytes = this.toByteArray(expected);
        var baseXEncodeService = new Base62EncodeService();

        // when
        var encoded = baseXEncodeService.encode(bytes);

        // then
        assertThat(encoded).isEqualTo(expected);
    }

    private byte[] toByteArray(String expected) {
        var codecList = Base62EncodeService.CODEC.chars()
                .mapToObj(c -> (char) c)
                .toList();
        var base = codecList.size();
        var charArray = expected.toCharArray();
        return IntStream.range(0, charArray.length)
                .mapToObj(i -> {
                    var c = charArray[i];
                    var cIndex = codecList.indexOf(c);
                    if (cIndex < 0) {
                        throw new RuntimeException("잘못된 codec 입니다.");
                    }
                    var a = BigInteger.valueOf(cIndex);

                    var j = charArray.length - 1 - i;
                    var b = BigInteger.valueOf(base).pow(j);

                    return a.multiply(b);
                })
                .reduce(BigInteger::add)
                .map(BigInteger::toByteArray)
                .orElse(new byte[]{});
    }
}