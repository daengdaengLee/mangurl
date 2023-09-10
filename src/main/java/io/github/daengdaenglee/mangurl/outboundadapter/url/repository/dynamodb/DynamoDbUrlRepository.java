package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import io.github.daengdaenglee.mangurl.application.url.outboundport.DuplicateShortUrlCodeException;
import io.github.daengdaenglee.mangurl.application.url.outboundport.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
// @TODO profile 을 통해 조건에 맞는 경우 Component 로 등록, 다른 조건인 경우 InMemoryUrlRepository 사용
@Primary
@Repository
class DynamoDbUrlRepository implements UrlRepository {
    private final UrlEntry.DynamoDb urlEntryDynamoDb;

    @Override
    public Optional<String> findShortUrlCodeByOriginalUrl(String originalUrl) {
        var key = Key.builder().partitionValue(originalUrl).build();
        var queryConditional = QueryConditional.keyEqualTo(key);
        var queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(1)
                .build();
        return this.urlEntryDynamoDb.originalUrlIndex()
                .query(queryRequest)
                .stream()
                .flatMap(p -> p.items().stream())
                .map(UrlEntry::getShortUrlCode)
                .findAny();
    }

    @Override
    public void save(String originalUrl, String shortUrlCode) {
        var urlEntry = UrlEntry.builder()
                .shortUrlCode(shortUrlCode)
                .originalUrl(originalUrl)
                .build();
        var request = PutItemEnhancedRequest.builder(UrlEntry.class)
                .item(urlEntry)
                .conditionExpression(Expression.builder()
                        .expression("attribute_not_exists(shortUrlCode)")
                        .build())
                .build();
        try {
            this.urlEntryDynamoDb.table().putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new DuplicateShortUrlCodeException();
        }
    }

    @Override
    public Optional<String> findOriginalUrlByShortUrlCode(String shortUrlCode) {
        var key = Key.builder().partitionValue(shortUrlCode).build();
        return Optional.ofNullable(this.urlEntryDynamoDb.table().getItem(key))
                .map(UrlEntry::getOriginalUrl);
    }
}
