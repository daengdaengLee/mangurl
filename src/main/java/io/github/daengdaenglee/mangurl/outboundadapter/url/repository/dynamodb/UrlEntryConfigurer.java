package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.dynamodb;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties.RepositoryProperties.DdlAuto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
@Conditional(DynamoDbCondition.class)
class UrlEntryConfigurer {
    private final MangurlProperties mangurlProperties;
    private final DynamoDbClient client;
    private final UrlEntry.DynamoDb urlEntryDynamoDb;

    @PostConstruct
    void postConstruct() {
        var ddlAuto = this.mangurlProperties.repository().ddlAuto();
        if (ddlAuto.contains(DdlAuto.DROP)) {
            this.drop();
        }
        if (ddlAuto.contains(DdlAuto.CREATE)) {
            this.create();
        }
        if (ddlAuto.contains(DdlAuto.VALIDATE)) {
            this.validate();
        }
    }

    private void drop() {
        var table = this.urlEntryDynamoDb.table();
        var tableName = table.tableName();
        var request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();
        try {
            this.client.deleteTable(request);
        } catch (ResourceNotFoundException ignored) {
        }
    }

    private void create() {
        var table = this.urlEntryDynamoDb.table();
        var tableName = table.tableName();

        this.urlEntryDynamoDb.table().createTable(builder -> builder
                .globalSecondaryIndices(gsi -> gsi
                        .indexName("OriginalUrlIndex")
                        .projection(p -> p.projectionType(ProjectionType.KEYS_ONLY))
                        .provisionedThroughput(b -> b
                                .readCapacityUnits(1L)
                                .writeCapacityUnits(1L)
                                .build()))
                .provisionedThroughput(b -> b
                        .readCapacityUnits(1L)
                        .writeCapacityUnits(1L)
                        .build()));

        try (var waiter = DynamoDbWaiter.builder().client(this.client).build()) {
            waiter.waitUntilTableExists(builder -> builder.tableName(tableName).build())
                    .matched()
                    .response()
                    .orElseThrow(() -> new RuntimeException(tableName + " 테이블 생성에 실패했습니다."));
        }
    }

    private void validate() {
        var table = this.urlEntryDynamoDb.table();
        var tableName = table.tableName();
        var tableMetadata = table.tableSchema().tableMetadata();

        log.info("DynamoDb {} 테이블 검사", tableName);

        var tableDescription = this.client
                .describeTable(DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build())
                .table();

        if (tableDescription == null) {
            throw new RuntimeException(tableName + " 테이블이 없습니다.");
        }

        if (tableDescription.tableStatus() != TableStatus.ACTIVE) {
            throw new RuntimeException(tableName + " 테이블이 ACTIVE 상태가 아닙니다.");
        }

        var attributeDefinitions = tableDescription.attributeDefinitions();
        var attributeValidator = this.createAttributeValidator(attributeDefinitions);

        var primaryPartitionKeyNameResult = Optional.ofNullable(tableMetadata.primaryPartitionKey());
        var primarySortKeyNameResult = tableMetadata.primarySortKey();
        var primaryKeyCount = java.util.stream.Stream.concat(primaryPartitionKeyNameResult.stream(), primarySortKeyNameResult.stream()).count();
        var primaryKeySchemaElements = tableDescription.keySchema();
        if (primaryKeySchemaElements.size() != primaryKeyCount) {
            throw new RuntimeException(tableName + " 테이블의 Primary Key Schema 가 맞지 않습니다.");
        }
        var primaryKeyValidator = this.createKeyValidator(primaryKeySchemaElements);
        primaryPartitionKeyNameResult.ifPresent(
                keyName -> {
                    var attributeType = tableMetadata.scalarAttributeType(keyName)
                            .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                    attributeValidator.accept(keyName, attributeType);
                    primaryKeyValidator.accept(keyName, KeyType.HASH);
                });
        primarySortKeyNameResult.ifPresent(
                keyName -> {
                    var attributeType = tableMetadata.scalarAttributeType(keyName)
                            .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                    attributeValidator.accept(keyName, attributeType);
                    primaryKeyValidator.accept(keyName, KeyType.RANGE);
                });

        var globalSecondaryIndexDescriptions = tableDescription.globalSecondaryIndexes();
        var localSecondaryIndexDescriptions = tableDescription.localSecondaryIndexes();
        tableMetadata.indices()
                .stream()
                .filter(indexMetadata -> !indexMetadata.name().equals("$PRIMARY_INDEX"))
                .forEach(indexMetadata -> {
                    var indexName = indexMetadata.name();
                    var indexPartitionKeyResult = indexMetadata.partitionKey();
                    var indexSortKeyResult = indexMetadata.sortKey();
                    var indexKeyCount = Stream.concat(indexPartitionKeyResult.stream(), indexSortKeyResult.stream()).count();

                    var gsiResult = globalSecondaryIndexDescriptions.stream()
                            .filter(gsi -> gsi.indexName().equals(indexName))
                            .findAny();
                    var lsiResult = localSecondaryIndexDescriptions.stream()
                            .filter(lsi -> lsi.indexName().equals(indexName))
                            .findAny();

                    if (gsiResult.isEmpty() && lsiResult.isEmpty()) {
                        throw new RuntimeException(tableName + " 테이블에 " + indexName + " 인덱스가 없습니다.");
                    }

                    gsiResult.ifPresent(index -> {
                        var indexKeySchemaElements = index.keySchema();
                        if (indexKeySchemaElements.size() != indexKeyCount) {
                            throw new RuntimeException(tableName + " 테이블의 " + indexName + " Index Key Schema 가 맞지 않습니다.");
                        }
                        var indexKeyValidator = this.createKeyValidator(indexKeySchemaElements);
                        indexPartitionKeyResult.ifPresent(indexPartitionKey -> {
                            var keyName = indexPartitionKey.name();
                            var attributeType = Optional.ofNullable(indexPartitionKey.attributeValueType().scalarAttributeType())
                                    .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                            attributeValidator.accept(keyName, attributeType);
                            indexKeyValidator.accept(keyName, KeyType.HASH);
                        });
                        indexSortKeyResult.ifPresent(indexSortKey -> {
                            var keyName = indexSortKey.name();
                            var attributeType = Optional.ofNullable(indexSortKey.attributeValueType().scalarAttributeType())
                                    .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                            attributeValidator.accept(keyName, attributeType);
                            indexKeyValidator.accept(keyName, KeyType.RANGE);
                        });
                    });

                    lsiResult.ifPresent(index -> {
                        var indexKeySchemaElements = index.keySchema();
                        if (indexKeySchemaElements.size() != indexKeyCount) {
                            throw new RuntimeException(tableName + " 테이블의 " + indexName + " Index Key Schema 가 맞지 않습니다.");
                        }
                        var indexKeyValidator = this.createKeyValidator(indexKeySchemaElements);
                        indexPartitionKeyResult.ifPresent(indexPartitionKey -> {
                            var keyName = indexPartitionKey.name();
                            var attributeType = Optional.ofNullable(indexPartitionKey.attributeValueType().scalarAttributeType())
                                    .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                            attributeValidator.accept(keyName, attributeType);
                            indexKeyValidator.accept(keyName, KeyType.HASH);
                        });
                        indexSortKeyResult.ifPresent(indexSortKey -> {
                            var keyName = indexSortKey.name();
                            var attributeType = Optional.ofNullable(indexSortKey.attributeValueType().scalarAttributeType())
                                    .orElseThrow(() -> new RuntimeException(tableName + " 테이블의 " + keyName + " 의 Scalar Attribute Type 을 알 수 없습니다."));
                            attributeValidator.accept(keyName, attributeType);
                            indexKeyValidator.accept(keyName, KeyType.RANGE);
                        });
                    });
                });
    }

    private BiConsumer<String, ScalarAttributeType> createAttributeValidator(List<AttributeDefinition> attributeDefinitions) {
        return (attributeName, attributeType) -> attributeDefinitions.stream()
                .filter(ad -> ad.attributeName().equals(attributeName))
                .findAny()
                .ifPresentOrElse(
                        attr -> {
                            if (attr.attributeType() != attributeType) {
                                throw new RuntimeException(attributeName + " 속성이 " + attributeType + " 타입이 아닙니다.");
                            }
                        },
                        () -> {
                            throw new RuntimeException(attributeName + " 속성이 없습니다.");
                        });
    }

    private BiConsumer<String, KeyType> createKeyValidator(List<KeySchemaElement> keySchemaElements) {
        return (keyName, keyType) -> keySchemaElements.stream()
                .filter(key -> key.attributeName().equals(keyName))
                .peek(key -> {
                    if (key.keyType() != keyType) {
                        throw new RuntimeException(keyName + " 이 " + keyType + " Key 타입이 아닙니다.");
                    }
                })
                .findAny()
                .orElseThrow(() -> new RuntimeException(keyName + " Key 가 없습니다."));
    }
}
