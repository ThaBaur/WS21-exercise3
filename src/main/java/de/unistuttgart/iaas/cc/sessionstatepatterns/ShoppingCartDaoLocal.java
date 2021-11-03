package de.unistuttgart.iaas.cc.sessionstatepatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class ShoppingCartDaoLocal implements ShoppingCartDao {
    
    private static final ShoppingCartDao shoppingCart = new ShoppingCartDaoLocal();
    
    private static final String TABLE_NAME = "shopping-cart";
    private static final String ENDPOINT = "http://localhost:8000";
    private static final String REGION = "eu-central-1";

    private final AmazonDynamoDB client;
    private Table table;

    public ShoppingCartDaoLocal() {
        this.client = AmazonDynamoDBAsyncClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
            .build();
        DynamoDB db = new DynamoDB(this.client);
        try {
            table = db.getTable(TABLE_NAME);
            System.out.println("Table exists: " + table.getTableName());
        } catch (Exception e) {
            createTable(db);
        }
    }

    private void createTable(DynamoDB db) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("element").withAttributeType(ScalarAttributeType.S));

        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName("element").withKeyType(KeyType.HASH));

        CreateTableRequest request = new CreateTableRequest()
            .withTableName(TABLE_NAME)
            .withKeySchema(keySchema)
            .withAttributeDefinitions(attributeDefinitions)
            .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L));
        
        table = db.createTable(request);

        try {
            table.waitForActive();
            System.out.println("Table created");
        } catch(InterruptedException e) {
            System.out.println("Table creation got interruppted");
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static ShoppingCartDao getShoppingCart() {
        return shoppingCart;
    }

    @Override
    public List<String> getAllShoppingCartItems() {
        System.out.println("Load Shopping Cart Items");
        List<String> result = new ArrayList<>();
        ScanRequest scan = new ScanRequest().withTableName(TABLE_NAME);
        ScanResult scanResult = client.scan(scan);
        List<Map<String, AttributeValue>> items = scanResult.getItems();
        for (Map<String, AttributeValue> map : items) {
            map.values().stream().forEach(value -> result.add(value.getS()));
        }
        return result;
    }

    @Override
    public void addShoppingCartItem(String item) {
        System.out.println("Put Shopping Cart Items");
        this.table.putItem(
            new Item().withPrimaryKey("element", item)
        );
    }
    
}
