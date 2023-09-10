package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
class UrlEntryConfig {
    @Bean
    UrlEntry.DynamoDb urlEntryDynamoDb(DynamoDbEnhancedClient client) {
        var table = client.table("UrlEntry", TableSchema.fromBean(UrlEntry.class));
        var originalUrlIndex = table.index("OriginalUrlIndex");
        return new UrlEntry.DynamoDb(table, originalUrlIndex);
    }
}
