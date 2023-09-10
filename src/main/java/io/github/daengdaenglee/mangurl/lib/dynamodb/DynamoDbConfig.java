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
import java.net.URISyntaxException;

@Slf4j
@Configuration
class DynamoDbConfig {
    @Bean
    DynamoDbClient dynamoDbClient(MangurlProperties mangurlProperties) throws URISyntaxException {
        var builder = DynamoDbClient.builder();

        mangurlProperties.aws()
                .credentials()
                .map(credentials -> AwsBasicCredentials.create(
                        credentials.accessKeyId(),
                        credentials.secretAccessKey()))
                .map(StaticCredentialsProvider::create)
                .ifPresent(builder::credentialsProvider);

        mangurlProperties.dynamodb().region().ifPresent(builder::region);

        if (mangurlProperties.dynamodb().endpoint().isPresent()) {
            builder.endpointOverride(new URI(mangurlProperties.dynamodb().endpoint().get()));
        }

        return builder.build();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}
