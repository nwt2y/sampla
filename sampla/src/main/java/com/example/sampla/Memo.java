package com.example.sampla;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class Memo {
    private String id;
    private String text;
    private String createdAt;

    @DynamoDbPartitionKey
    public String getId() { return id; }
}