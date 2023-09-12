package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamoDbBean
public class UrlEntry {
    static final String ORIGINAL_URL_INDEX_NAME = "OriginalUrlIndex";
    @Getter(onMethod = @__({
            @DynamoDbPartitionKey,
            @DynamoDbSecondarySortKey(indexNames = UrlEntry.ORIGINAL_URL_INDEX_NAME)}))
    @Setter
    private String shortUrlCode;
    @Getter(onMethod = @__({@DynamoDbSecondaryPartitionKey(indexNames = UrlEntry.ORIGINAL_URL_INDEX_NAME)}))
    @Setter
    private String originalUrl;

    record DynamoDb(DynamoDbTable<UrlEntry> table, DynamoDbIndex<UrlEntry> originalUrlIndex) {
    }
}
