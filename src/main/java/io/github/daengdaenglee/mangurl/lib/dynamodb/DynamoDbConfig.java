package io.github.daengdaenglee.mangurl.lib.dynamodb;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Slf4j
@Configuration
class DynamoDbConfig {
    @Bean
    DynamoDbClient dynamoDbClient(MangurlProperties mangurlProperties) {
        var builder = DynamoDbClient.builder();

        builder.region(mangurlProperties.getAws().getRegion());

        mangurlProperties.getAws()
                .getCredentials()
                .map(credentials -> AwsBasicCredentials.create(
                        credentials.getAccessKeyId(),
                        credentials.getSecretAccessKey()))
                .map(StaticCredentialsProvider::create)
                .ifPresent(builder::credentialsProvider);

        mangurlProperties.getAws()
                .getDynamoDb()
                .flatMap(MangurlProperties.AwsProperties.DynamoDbProperties::getEndpoint)
                .map(URI::create)
                .ifPresent(builder::endpointOverride);

        return builder.build();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}
