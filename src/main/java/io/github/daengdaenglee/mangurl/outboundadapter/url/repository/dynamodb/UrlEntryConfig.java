package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties.RepositoryProperties.Type;
import io.github.daengdaenglee.mangurl.outboundadapter.url.repository.condition.ConditionalOnRepositoryType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
@ConditionalOnRepositoryType(Type.DYNAMODB)
class UrlEntryConfig {
    @Bean
    UrlEntry.DynamoDb urlEntryDynamoDb(
            MangurlProperties mangurlProperties,
            DynamoDbEnhancedClient client) {
        var tableName = mangurlProperties.getRepository().getTableNamePrefix() + UrlEntry.TABLE_NAME;
        var table = client.table(tableName, TableSchema.fromBean(UrlEntry.class));
        return new UrlEntry.DynamoDb(table, table.index(UrlEntry.ORIGINAL_URL_INDEX_NAME));
    }
}
