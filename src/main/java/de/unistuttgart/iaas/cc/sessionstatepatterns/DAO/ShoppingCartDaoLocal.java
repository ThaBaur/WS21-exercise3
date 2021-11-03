package de.unistuttgart.iaas.cc.sessionstatepatterns.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
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
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoppingCartDaoLocal implements ShoppingCartDao {
    
    private static final ShoppingCartDao shoppingCart = new ShoppingCartDaoLocal();
    
    private static final String TABLE_NAME = "shopping-cart";
    private static final String ENDPOINT = "http://localhost:8000";
    private static final Regions REGION = Regions.EU_CENTRAL_1;

    private final AmazonDynamoDB client;
    private Table table;

    private final static Logger logger = LoggerFactory.getLogger(ShoppingCartDaoLocal.class);

    public ShoppingCartDaoLocal() {
        this.client = AmazonDynamoDBAsyncClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION.name()))
            .build();
        DynamoDB db = new DynamoDB(this.client);
        try {
            table = db.getTable(TABLE_NAME);
        } catch (Exception e) {
            createTable(db);    
        }
    }

    private void createTable(DynamoDB db) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("element"));

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
            logger.info("Table created");
        } catch(InterruptedException e) {
            logger.error("Table creation got interruppted", e);
        }
    }

    public static ShoppingCartDao getShoppingCart() {
        return shoppingCart;
    }

    @Override
    public List<String> getAllShoppingCartItems() {
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
        this.table.putItem(
            new Item().withPrimaryKey("element", item)
        );   
    }
    
}
