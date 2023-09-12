package io.github.daengdaenglee.mangurl.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

import java.util.Optional;
import java.util.Set;

// 자동으로 자식 객체까지 검사
@Validated
@ConfigurationProperties(prefix = "mangurl", ignoreInvalidFields = true)
@ConfigurationPropertiesBinding
public record MangurlProperties(
        @NotNull
        String origin,
        @NotNull @NestedConfigurationProperty RepositoryProperties repository,
        @NestedConfigurationProperty Optional<AwsProperties> aws) {
    @ConstructorBinding
    public MangurlProperties(
            String origin,
            RepositoryProperties repository,
            AwsProperties aws) {
        this(
                origin == null ? "" : origin,
                repository,
                Optional.ofNullable(aws));
    }

    @ConfigurationPropertiesBinding
    public record RepositoryProperties(
            @NotNull Type type,
            Optional<String> tableNamePrefix,
            Set<DdlAuto> ddlAuto) {
        public enum Type {
            IN_MEMORY,
            DYNAMODB
        }

        public enum DdlAuto {
            DROP,
            CREATE,
            VALIDATE
        }
    }

    @ConfigurationPropertiesBinding
    public record AwsProperties(
            @NotNull Region region,
            Optional<CredentialsProperties> credentials,
            Optional<DynamoDbProperties> dynamoDb) {
        @ConstructorBinding
        public AwsProperties(
                String region,
                CredentialsProperties credentials,
                DynamoDbProperties dynamoDb) {
            this(
                    Region.regions()
                            .stream()
                            .filter(r -> r.toString().equals(region))
                            .findAny()
                            .orElse(null),
                    Optional.ofNullable(credentials),
                    Optional.ofNullable(dynamoDb));

        }

        @ConfigurationPropertiesBinding
        public record CredentialsProperties(@NotNull String accessKeyId, @NotNull String secretAccessKey) {
        }

        @ConfigurationPropertiesBinding
        public record DynamoDbProperties(Optional<String> endpoint) {
        }
    }
}
