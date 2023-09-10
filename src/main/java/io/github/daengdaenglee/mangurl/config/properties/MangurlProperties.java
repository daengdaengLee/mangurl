package io.github.daengdaenglee.mangurl.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import software.amazon.awssdk.regions.Region;

import java.util.Optional;
import java.util.Set;

@Slf4j
@ConfigurationProperties(prefix = "mangurl")
@ConfigurationPropertiesBinding
public record MangurlProperties(
        String origin,
        @NestedConfigurationProperty RepositoryProperties repository,
        @NestedConfigurationProperty AwsProperties aws,
        @NestedConfigurationProperty DynamoDbProperties dynamodb) {
    public MangurlProperties(
            String origin,
            RepositoryProperties repository,
            AwsProperties aws,
            DynamoDbProperties dynamodb) {
        this.origin = origin == null ? "" : origin;
        this.repository = repository == null ?
                new RepositoryProperties(null, null) :
                repository;
        this.aws = aws == null ?
                new AwsProperties(Optional.empty()) :
                aws;
        this.dynamodb = dynamodb == null ?
                new DynamoDbProperties(Optional.empty(), Optional.empty()) :
                dynamodb;
    }

    @ConfigurationPropertiesBinding
    public record RepositoryProperties(Type type, Set<DdlAuto> ddlAuto) {
        public RepositoryProperties(Type type, Set<DdlAuto> ddlAuto) {
            this.type = type == null ? Type.IN_MEMORY : type;
            this.ddlAuto = ddlAuto == null ? Set.of() : Set.copyOf(ddlAuto);
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
    public record AwsProperties(Optional<CredentialsProperties> credentials) {
        @ConstructorBinding
        public AwsProperties(CredentialsProperties credentials) {
            this(Optional.ofNullable(credentials));
        }

        @ConfigurationPropertiesBinding
        public record CredentialsProperties(String accessKeyId, String secretAccessKey) {
            public CredentialsProperties(String accessKeyId, String secretAccessKey) {
                this.accessKeyId = accessKeyId == null ? "" : accessKeyId;
                this.secretAccessKey = secretAccessKey == null ? "" : secretAccessKey;
            }
        }
    }

    @ConfigurationPropertiesBinding
    public record DynamoDbProperties(Optional<Region> region, Optional<String> endpoint) {
        @ConstructorBinding
        public DynamoDbProperties(Region region, String endpoint) {
            this(Optional.ofNullable(region), Optional.ofNullable(endpoint));
        }
    }

    @PostConstruct
    void test() {
        log.info("test = {}", this);
    }
}
