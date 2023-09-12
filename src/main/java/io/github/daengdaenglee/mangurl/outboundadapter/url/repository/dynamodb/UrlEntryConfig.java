package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
@Conditional(DynamoDbCondition.class)
class UrlEntryConfig {
    @Bean
    UrlEntry.DynamoDb urlEntryDynamoDb(
            MangurlProperties mangurlProperties,
            DynamoDbEnhancedClient client) {
        var table = client.table(
                mangurlProperties.repository().tableNamePrefix().orElse("") + UrlEntry.TABLE_NAME,
                TableSchema.fromBean(UrlEntry.class));
        return new UrlEntry.DynamoDb(table, table.index(UrlEntry.ORIGINAL_URL_INDEX_NAME));
    }
}
