package s.com.videoapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import s.com.videoapp.R;
import s.com.videoapp.aws.AmazonClientManager;
import s.com.videoapp.aws.CrashHandler;
import s.com.videoapp.aws.DatabaseAccess;
import s.com.videoapp.aws.DynamoDBManager;
import s.com.videoapp.databinding.ActivityMainBinding;
import s.com.videoapp.databinding.RowVideoBinding;
import s.com.videoapp.pojo.VideoItem;
import s.com.videoapp.utils.Constants;
import s.com.videoapp.utils.ImageFilePath;
import s.com.videoapp.utils.StoreUserData;
import s.com.videoapp.utils.Utils;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyBee6u0yAUUV6kiqA9W0u3YH1rG_WTKCpo";

    Activity activity;
    int duration;
    String videoId;
    ActivityMainBinding binding;
    StoreUserData storeUserData;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    YouTubePlayer youTubePlayer;
    VideoAdapter adapter;
    ArrayList<VideoItem.Tasks> tasktArray;

    private AmazonClientManager amazonClientManager;
    private DynamoDBManager dynamoDBManager;

    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_main);
        binding.youtubePlayer.initialize(API_KEY, (YouTubePlayer.OnInitializedListener) activity);

        videoId = getIntent().getStringExtra("videoID");

        tasktArray = (ArrayList<VideoItem.Tasks>) getIntent().getSerializableExtra("taskArray");

        Log.i("taskArray", tasktArray.toString());
        Log.i("videoId", videoId);
        binding.btnSnapShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(MainActivity.this, VideoSnapActivity.class);
                startActivity(intent);*/
               if(!storeUserData.getBoolean(Constants.IS_LOGGED_IN)){
                   Utils.showAlert(activity,"Please login to upload file.");
                   return;
               }
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            11);
                } else {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), 101);
                }
            }
        });


//        this is a share with facebook mock
            // Finding the facebook share button
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
            // Sharing the content to facebook
        ShareLinkContent content = new ShareLinkContent.Builder()
                // Setting the title that will be shared
                .setContentTitle("I am HT")
                // Setting the description that will be shared
                .setContentDescription("We did it!")
                // Setting the URL that will be shared
                .setContentUrl(Uri.parse("https://justa128.github.io/dubai-tour-guide/landingpage/"))
                // Setting the image that will be shared
                .setImageUrl(Uri.parse("https://img.freepik.com/free-vector/tel-aviv-skyline_1116-182.jpg"))
                .build();
        shareButton.setShareContent(content);


            binding.tvTitle.setText(getIntent().getStringExtra("videoTitle"));

            adapter = new VideoAdapter(activity, tasktArray);
            binding.rvVideo.setLayoutManager(new GridLayoutManager(this, 3));
            binding.rvVideo.setAdapter(adapter);


        setupAWSMobileClient();


    }

    private void setupAWSMobileClient() {
        try {
            CrashHandler.installHandler(this);
            new GetDataTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadWithTransferUtility(String path) {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload(
                        videoId.substring(17) + "/" + System.currentTimeMillis() + new File(path).getName(),
                        new File(path));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                if(percentDone==100){
                    Utils.showAlert(activity,"File uplaoded successfully.");
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("YourActivity", "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }


    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                databaseAccess = DatabaseAccess.getInstance(activity);
                List<VideoItem> documentList = databaseAccess.getAllVideos();

                Log.d("TestData", "Data : " + documentList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (null == youTubePlayer) return;
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            this.youTubePlayer.cueVideo(videoId.substring(17));
        }
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(activity, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            getYouTubePlayerProvider().initialize(API_KEY, this);
        } else if (requestCode == 101) {
            if (null == data) {
                Log.i("data", "null");
                return;
            }
            String selectedImagePath;
            Uri selectedImageUri = data.getData();
            //MEDIA GALLERY
            if (selectedImageUri == null)
                Log.i("Image File Path", "null");
            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
            uploadWithTransferUtility(selectedImagePath);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

        private List<VideoItem.Tasks> videoData;
        Activity activity;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RowVideoBinding binding;

            public MyViewHolder(View view) {
                super(view);
                binding = DataBindingUtil.bind(view);
            }
        }


        public VideoAdapter(Activity activity, List<VideoItem.Tasks> videoData) {
            this.videoData = videoData;
            this.activity = activity;
        }

        @Override
        public VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_video, parent, false);
            return new VideoAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final VideoAdapter.MyViewHolder holder, final int position) {

            final VideoItem.Tasks videoinfo = videoData.get(position);
            holder.binding.tvVideoTitle.setText(videoinfo.text);
            holder.binding.tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("message", "ok");

                    final Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setContentView(R.layout.dialog);

                    TextView text = dialog.findViewById(R.id.tvVideoTitle);
                    text.setText(videoinfo.text);
                    ImageView videoImage = dialog.findViewById(R.id.imgVideo);

                    Glide.with(activity).
                            load(videoinfo.link).
                            into(videoImage);

                    TextView step = dialog.findViewById(R.id.tvStep);
                    text.setText(videoinfo.text);

                    int stp = position + 1;
                    for (int i = 0; i < stp; i++) {
                        step.setText("Step " + stp);
                    }

                    ImageView closeImage = dialog.findViewById(R.id.imgClose);
                    closeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            Glide.with(activity).
                    load(videoinfo.link).
                    into(holder.binding.imgVideo);

            int size = position + 1;
            for (int i = 0; i < size; i++) {
                holder.binding.tvStep.setText("Step " + size);
            }

            holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String time = videoinfo.start;

                    String[] units = time.split(":");
                    int minutes = Integer.parseInt(units[0]);
                    int seconds = Integer.parseInt(units[1]);
                    duration = 60 * minutes + seconds;
                    youTubePlayer.seekToMillis(duration * 1000);
                    youTubePlayer.play();
                }
            });
        }

        @Override
        public int getItemCount() {

            return videoData == null ? 0 : videoData.size();
        }
    }
}


