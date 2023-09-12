package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
@Conditional(DynamoDbCondition.class)
class UrlEntryConfig {
    @Bean
    UrlEntry.DynamoDb urlEntryDynamoDb(DynamoDbEnhancedClient client) {
        var table = client.table("UrlEntry", TableSchema.fromBean(UrlEntry.class));
        var originalUrlIndex = table.index(UrlEntry.ORIGINAL_URL_INDEX_NAME);
        return new UrlEntry.DynamoDb(table, originalUrlIndex);
    }
}
