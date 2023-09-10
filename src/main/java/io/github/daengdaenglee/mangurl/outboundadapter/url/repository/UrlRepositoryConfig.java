package io.github.daengdaenglee.mangurl.outboundadapter.url.repository;

import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb.DynamoDbUrlRepository;
import io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb.UrlEntry;
import io.github.daengdaenglee.mangurl.outboundadapter.url.repository.inmemory.InMemoryUrlRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UrlRepositoryConfig {
    @Bean
    UrlRepository urlRepository(
            MangurlProperties mangurlProperties,
            UrlEntry.DynamoDb urlEntryDynamoDb) {
        var type = mangurlProperties.repository().type();
        switch (type) {
            case IN_MEMORY -> {
                return new InMemoryUrlRepository();
            }
            case DYNAMODB -> {
                return new DynamoDbUrlRepository(urlEntryDynamoDb);
            }
            default -> throw new RuntimeException(type + "에 맞는 UrlRepository 객체를 생성할 수 없습니다.");
        }
    }
}
