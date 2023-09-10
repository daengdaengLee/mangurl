package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
class DynamoDbConfig {
    @Bean
    DynamoDbClient dynamoDbClient() throws URISyntaxException {
        return DynamoDbClient.builder()
                // @TODO application.yml 설정을 통해 필요한 경우 credentials provider 설정
                //       운영 환경에서는 기본 자격 증명 공급자 체인(default credentials provider chain)을 사용 -> 별도의 인증 정보 추가 X
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                // @TODO application.yml 설정을 통해 필요한 경우 endpoint 설정
                .endpointOverride(new URI("http://localhost:8000"))
                // @TODO application.yml 설정을 통해 region 설정
                // 서울 리전
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}
