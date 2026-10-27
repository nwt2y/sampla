package com.example.sampla;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class AppUser {
    private String username;
    private String passwordHash;

    @DynamoDbPartitionKey
    public String getUsername() { return username; }
}