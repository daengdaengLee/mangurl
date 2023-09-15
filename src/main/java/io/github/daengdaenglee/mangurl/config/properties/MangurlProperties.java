package io.github.daengdaenglee.mangurl.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

import java.util.Optional;
import java.util.Set;

@Getter
// 자동으로 자식 객체까지 검사
@Validated
@ConfigurationProperties(prefix = "mangurl", ignoreInvalidFields = true)
@ConfigurationPropertiesBinding
public class MangurlProperties {
    @NotNull(message = "mangurl.origin 설정이 없습니다.")
    private final String origin;
    @NotNull(message = "mangurl.repository 설정이 없습니다.")
    @NestedConfigurationProperty
    private final RepositoryProperties repository;
    @NotNull(message = "mangurl.aws 설정이 없습니다.")
    @NestedConfigurationProperty
    private final AwsProperties aws;

    @ConstructorBinding
    public MangurlProperties(String origin, RepositoryProperties repository, AwsProperties aws) {
        this.origin = origin;
        this.repository = repository;
        this.aws = aws == null ? new AwsProperties() : aws;
    }

    @Getter
    @ConfigurationPropertiesBinding
    public static class RepositoryProperties {
        @NotNull(message = "mangurl.repository.type 설정이 없습니다.")
        private final Type type;
        @NotNull(message = "mangurl.repository.tableNamePrefix 설정이 없습니다.")
        private final String tableNamePrefix;
        @NotNull(message = "mangurl.repository.ddlAuto 설정이 없습니다.")
        private final Set<DdlAuto> ddlAuto;

        @ConstructorBinding
        public RepositoryProperties(Type type, String tableNamePrefix, Set<DdlAuto> ddlAuto) {
            this.type = type == null ? Type.IN_MEMORY : type;
            this.tableNamePrefix = tableNamePrefix == null ? "" : tableNamePrefix;
            this.ddlAuto = ddlAuto == null ? Set.of() : ddlAuto;
        }

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
    public static class AwsProperties {
        @Getter
        @NotNull
        private final Region region;
        private final CredentialsProperties credentials;
        private final DynamoDbProperties dynamoDb;

        public AwsProperties(Region region, CredentialsProperties credentials, DynamoDbProperties dynamoDb) {
            this.region = region;
            this.credentials = credentials;
            this.dynamoDb = dynamoDb;
        }

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
                    credentials,
                    dynamoDb);
        }

        public AwsProperties() {
            this(Region.AP_NORTHEAST_2, null, null);
        }

        public Optional<CredentialsProperties> getCredentials() {
            return Optional.ofNullable(this.credentials);
        }

        public Optional<DynamoDbProperties> getDynamoDb() {
            return Optional.ofNullable(this.dynamoDb);
        }

        @RequiredArgsConstructor(onConstructor = @__(@ConstructorBinding))
        @Getter
        @ConfigurationPropertiesBinding
        public static class CredentialsProperties {
            @NotNull
            private final String accessKeyId;
            @NotNull
            private final String secretAccessKey;
        }

        @RequiredArgsConstructor(onConstructor = @__(@ConstructorBinding))
        @ConfigurationPropertiesBinding
        public static class DynamoDbProperties {
            private final String endpoint;

            public Optional<String> getEndpoint() {
                return Optional.ofNullable(this.endpoint);
            }
        }
    }
}
