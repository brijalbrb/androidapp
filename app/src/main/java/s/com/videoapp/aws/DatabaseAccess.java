package s.com.videoapp.aws;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import s.com.videoapp.pojo.VideoItem;
import s.com.videoapp.utils.Constants;

public class DatabaseAccess {
    /**
     * The Amazon Cognito POOL_ID to use for authentication and authorization.
     */
    private final String COGNITO_POOL_ID = Constants.IDENTITY_POOL_ID;

    /**
     * The AWS Region that corresponds to the POOL_ID above
     */
    private final Regions COGNITO_REGION = Regions.US_EAST_1;

    /**
     * The name of the DynamoDB table used to store data.  If using AWS Mobile Hub, then note
     * that this is the "long name" of the table, as specified in the Resources section of
     * the console.  This should be defined with the Notes schema.
     */
    private final String DYNAMODB_TABLE = Constants.TEST_TABLE_NAME;

    /**
     * The Android calling context
     */
    private Context context;

    /**
     * The Cognito Credentials Provider
     */
    private CognitoCachingCredentialsProvider credentialsProvider;

    /**
     * A connection to the DynamoDD service
     */
    private AmazonDynamoDBClient dbClient;

    /**
     * A reference to the DynamoDB table used to store data
     */
    private Table dbTable;

    /**
     * This class is a singleton - storage for the current instance.
     */
    private static volatile DatabaseAccess instance;

    private DynamoDBMapper dynamoDBMapper;

    /**
     * Creates a new DatabaseAccess instance.
     *
     * @param context the calling context
     */
    private DatabaseAccess(Context context) {
        this.context = context;

        // Create a new credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(context,
                COGNITO_POOL_ID,
                COGNITO_REGION);

//        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // Create a connection to the DynamoDB service
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        // Create a table reference

        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);
    }

    /**
     * Singleton pattern - retrieve an instance of the DatabaseAccess
     */
    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * create a new video in the database
     *
     * @param video the video to create
     */
    public void create(Document video) {
        if (!video.containsKey("userId")) {
            video.put("userId", credentialsProvider.getCachedIdentityId());
        }
        if (!video.containsKey("videoId")) {
            video.put("videoId", UUID.randomUUID().toString());
        }
        if (!video.containsKey("creationDate")) {
            video.put("creationDate", System.currentTimeMillis());
        }
        dbTable.putItem(video);
    }

    /**
     * Update an existing video in the database
     *
     * @param video the video to save
     */
    public void update(Document video) {
        Document document = dbTable.updateItem(video, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }

    /**
     * Delete an existing video in the database
     *
     * @param video the video to delete
     */
    public void delete(Document video) {
        dbTable.deleteItem(video.get("userId").asPrimitive(), video.get("videoId").asPrimitive());
    }

    /**
     * Retrieve a video by videoId from the database
     *
     * @param videoId the ID of the note
     * @return the related document
     */
    public Document getVideoById(String videoId) {
        return dbTable.getItem(new Primitive(credentialsProvider.getCachedIdentityId()), new Primitive(videoId));
    }

    /**
     * Retrieve all the videos from the database
     *
     * @return the list of videos
     */
    public List<VideoItem> getAllVideos() {

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DYNAMODB_TABLE);
        ScanResult result = dbClient.scan(scanRequest);

        List<VideoItem> videoItems = new ArrayList<>();

        for (Map<String, AttributeValue> item : result.getItems()){
            //result.getItems().get(0).get("link").getS()
            //result.getItems().get(0).get("videoThumbnail").getS()
            //result.getItems().get(0).get("year").getN()

            //videoItems.add();
        }

        return videoItems;
    }
}