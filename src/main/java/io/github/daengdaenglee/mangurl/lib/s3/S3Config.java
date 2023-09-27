package io.github.daengdaenglee.mangurl.lib.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.util.Optional;

@Configuration
public class S3Config {
    @Bean
    @ConditionalOnBean(Region.class)
    public S3AsyncClient s3AsyncClient(Region region, Optional<AwsCredentials> awsCredentials) {
        var builder = S3AsyncClient.builder();
        builder.region(region);
        awsCredentials.map(StaticCredentialsProvider::create).ifPresent(builder::credentialsProvider);
        return builder.build();
    }

    @Bean
    @ConditionalOnBean(S3AsyncClient.class)
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder().s3Client(s3AsyncClient).build();
    }
}
