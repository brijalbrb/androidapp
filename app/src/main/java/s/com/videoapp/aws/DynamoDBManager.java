package s.com.videoapp.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import s.com.videoapp.utils.Constants;

public class DynamoDBManager {

    private Context context;

    private AmazonClientManager amazonClientManager;

    public DynamoDBManager(Context context) {
        this.context = context;
        amazonClientManager = new AmazonClientManager(context);
    }

    public void createTable() {
        try {
            AmazonDynamoDBClient amazonDynamoDBClient = amazonClientManager.ddb();

            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(Constants.TEST_TABLE_NAME);

            amazonDynamoDBClient.createTable(createTableRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData() {
        try {
            AmazonDynamoDBClient ddb = amazonClientManager.ddb();

//            Map<String, String> stringMap = new HashMap<>();

//            ddb.putItem(Constants.TEST_TABLE_NAME, stringMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, AttributeValue>> getData() {
        try {
            AmazonDynamoDBClient ddb = amazonClientManager.ddb();
            QueryRequest queryRequest = new QueryRequest();
            queryRequest.setTableName(Constants.TEST_TABLE_NAME);
            queryRequest.setKeyConditionExpression("userId");

            List<Map<String, AttributeValue>> mapList = new ArrayList<>();

            Log.d("TestData", "Data : " + ddb.query(queryRequest).getItems().size());

            mapList = ddb.query(queryRequest).getItems();

            return mapList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
