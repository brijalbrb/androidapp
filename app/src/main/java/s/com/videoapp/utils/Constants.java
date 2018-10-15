package s.com.videoapp.utils;

import com.amazonaws.regions.Regions;

public class Constants {

    public static final String URL = "http://13.251.224.176";
    
    public static final String USER_IMEI = "USER_IMEI";
    public static final String USER_FCM = "USER_FCM";
    public static final String USER_ID = "USER_ID";
    public static final String START = "START";
    public static final String UPDATE_LOC_TIME = "UPDATE_LOC_TIME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_LAST_NAME = "USER_LAST_NAME";
    public static final String CURRENT_REQUEST = "CURRENT_REQUEST";
    public static final String SERVED_REQUEST = "SERVED_REQUEST";
    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    //TODO: AWS Credentials
    public static final String IDENTITY_POOL_ID = "us-east-1:d63115a9-5bb4-4789-8631-b66043235db6";
    public static final Regions COGNITO_REGION = Regions.US_EAST_1;  // Set your Cognito region if is different

    // Note that spaces are not allowed in the table name
    public static final String TEST_TABLE_NAME = "diyvi-mobilehub-1727673198-diyvids";
    public static final Regions DYNAMODB_REGION = Regions.US_EAST_1;  // Set your DynamoDB region if is different

 
}