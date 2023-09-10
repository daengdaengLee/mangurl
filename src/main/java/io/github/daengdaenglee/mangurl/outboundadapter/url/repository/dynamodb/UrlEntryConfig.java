package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class UrlEntryConfig {
    @Bean
    TableSchema<UrlEntry> urlEntryTableSchema() {
        return TableSchema.fromBean(UrlEntry.class);
    }

    @Bean
    UrlEntry.DynamoDb urlEntryDynamoDb(DynamoDbEnhancedClient client, TableSchema<UrlEntry> schema) {
        var table = client.table("UrlEntry", schema);
        var originalUrlIndex = table.index("OriginalUrlIndex");
        return new UrlEntry.DynamoDb(table, originalUrlIndex);
    }
}
