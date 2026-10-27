package com.example.sampla;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Repository
public class UserRepository {

    private static final String TABLE = "app_user";
    private final DynamoDbClient client;
    private final DynamoDbTable<AppUser> table;

    public UserRepository(DynamoDbEnhancedClient enhanced, DynamoDbClient client) {
        this.client = client;
        this.table = enhanced.table(TABLE, TableSchema.fromBean(AppUser.class));
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

    public Optional<AppUser> findByUsername(String username) {
        return Optional.ofNullable(
                table.getItem(Key.builder().partitionValue(username).build()));
    }

    /** 同じユーザー名がすでにあれば false を返す */
    public boolean create(AppUser user) {
        try {
            table.putItem(r -> r.item(user).conditionExpression(
                    Expression.builder()
                            .expression("attribute_not_exists(username)")
                            .build()));
            return true;
        } catch (ConditionalCheckFailedException e) {
            return false;
        }
    }
}