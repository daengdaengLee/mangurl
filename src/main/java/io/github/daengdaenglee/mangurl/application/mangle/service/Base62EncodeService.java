package io.github.daengdaenglee.mangurl.application.mangle.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.LinkedList;

@Service
class Base62EncodeService {
    static final String CODEC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final char[] codec = CODEC.toCharArray();
    private final BigInteger base = new BigInteger(String.valueOf(this.codec.length));

    String encode(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }

        var paddedBytes = this.padLeadingZero(bytes);
        var value = new BigInteger(paddedBytes);

        var buffer = new LinkedList<Character>();
        while (value.compareTo(this.base) > 0) {
            var c = this.codec[value.remainder(this.base).intValue()];
            buffer.addFirst(c);
            value = value.divide(this.base);
        }
        buffer.addFirst(this.codec[value.intValue()]);

        var sb = new StringBuilder();
        buffer.forEach(sb::append);
        return sb.toString();
    }

    /**
     * BigInteger 로 변환했을 때 항상 양수로 만들기 위해 부호 바이트 0 을 항상 추가
     * BigInteger 는 big 엔디언 방식으로 동작하므로 앞에 0 을 붙이는 건 값 변경은 없음
     */
    private byte[] padLeadingZero(byte[] bytes) {
        var padded = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, padded, 1, bytes.length);
        return padded;
    }
}
