package com.example.sampla;


import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Repository
public class MemoRepository {

    private static final String TABLE = "memo";
    private final DynamoDbClient client;
    private final DynamoDbTable<Memo> table;

    public MemoRepository(DynamoDbEnhancedClient enhanced, DynamoDbClient client) {
        this.client = client;
        this.table = enhanced.table(TABLE, TableSchema.fromBean(Memo.class));
    }

    @PostConstruct
    void init() {
        try {
            table.describeTable();
        } catch (ResourceNotFoundException e) {
            table.createTable();
            client.waiter().waitUntilTableExists(b -> b.tableName(TABLE));
        }
    }

    public List<Memo> findAll() {
        return table.scan().items().stream()
                .sorted(Comparator.comparing(Memo::getCreatedAt).reversed())
                .toList();
    }

    public void save(Memo memo) {
        table.putItem(memo);
    }

    public void delete(String id) {
        table.deleteItem(Key.builder().partitionValue(id).build());
    }
}