package s.com.videoapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import s.com.videoapp.R;
import s.com.videoapp.aws.CrashHandler;
import s.com.videoapp.aws.DatabaseAccess;
import s.com.videoapp.databinding.ActivityVideoSnapBindingImpl;
import s.com.videoapp.databinding.RowVideoBinding;
import s.com.videoapp.pojo.VideoItem;
import s.com.videoapp.pojo.Videopojo;
import s.com.videoapp.utils.StoreUserData;

public class VideoSnapActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyBee6u0yAUUV6kiqA9W0u3YH1rG_WTKCpo";
    public String VIDEO_ID = "RAOJ0b0iWBg";
    Button btnSave, btnDelete, btnSnap;
    ImageView ivSnaps;
    Activity activity;
    int duration;
    ActivityVideoSnapBindingImpl binding;
    StoreUserData storeUserData;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    YouTubePlayer youTubePlayer;
    VideoAdapter adapter;
    Bitmap bitmap;
    DatabaseAccess databaseAccess;
    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_snap);
        mediaMetadataRetriever = new MediaMetadataRetriever();
        btnSnap = findViewById(R.id.btn_snapShot);
        btnSave = findViewById(R.id.btn_saveSnap);
        btnDelete = (Button) findViewById(R.id.btn_deleteSnap);
        ivSnaps = findViewById(R.id.ivSnapShot);
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_video_snap);
        binding.youtubePlayerS.initialize(API_KEY, (YouTubePlayer.OnInitializedListener) activity);

        binding.btnSnapShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v1 = binding.youtubePlayerS;
                v1.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);
                binding.ivSnapShot.setImageBitmap(bitmap);
            }
        });

        binding.btnSaveSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                File file = wrapper.getDir("Images", MODE_PRIVATE);
                file = new File(file, System.currentTimeMillis() + ".jpg");
                try {
                    OutputStream stream = null;
                    stream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri savedImageURI = Uri.parse(file.getAbsolutePath());


                Toast.makeText(getApplicationContext(), "Image has been Saved to\n " + savedImageURI, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDeleteSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivSnapShot.setImageBitmap(null);
            }
        });

        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Videopojo request = gson.fromJson(obj.toString(), Videopojo.class);
            adapter = new VideoAdapter(activity, request.getArray());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupAWSMobileClient();
    }

    private void setupAWSMobileClient() {
        try {

            CrashHandler.installHandler(this);

            new VideoSnapActivity.GetDataTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap createVideoThumbnail(Context context, Uri uri, int i) {
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();

        try {
            mediametadataretriever.setDataSource(context, uri);
            Bitmap bitmap = mediametadataretriever.getFrameAtTime(-1L);
            if (null != bitmap) {

                return ThumbnailUtils.extractThumbnail(bitmap, 150, 150, 2);
            }
            return bitmap;
        } catch (Throwable t) {
            // TODO log
            return null;
        } finally {
            try {
                mediametadataretriever.release();
            } catch (RuntimeException e) {
            }
        }
    }


    public static Bitmap takeSnapshot(View givenView, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas snap = new Canvas(bm);
        givenView.layout(0, 0, givenView.getLayoutParams().width, givenView.getLayoutParams().height);
        givenView.draw(snap);
        return bm;
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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("videoapi.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @SuppressLint("NewApi")
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (null == youTubePlayer) return;
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            this.youTubePlayer.cueVideo(VIDEO_ID);

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
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    public class VideoAdapter extends RecyclerView.Adapter<VideoSnapActivity.VideoAdapter.MyViewHolder> {

        private List<Videopojo.ArrayBean> videoData;
        Activity activity;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            RowVideoBinding binding;

            public MyViewHolder(View view) {
                super(view);
                binding = DataBindingUtil.bind(view);
            }
        }


        public VideoAdapter(Activity activity, List<Videopojo.ArrayBean> videoData) {
            this.videoData = videoData;
            this.activity = activity;
        }

        @Override
        public VideoSnapActivity.VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_video, parent, false);
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull VideoSnapActivity.VideoAdapter.MyViewHolder holder, final int position) {

            final Videopojo.ArrayBean videoinfo = videoData.get(position);

            holder.binding.tvVideoTitle.setText(videoinfo.getText());
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
                    text.setText(videoinfo.getText());

                    ImageView videoImage = dialog.findViewById(R.id.imgVideo);

                    Glide.with(activity).
                            load(videoinfo.getLink()).
                            into(videoImage);

                    TextView step = dialog.findViewById(R.id.tvStep);
                    text.setText(videoinfo.getText());

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
                    load(videoinfo.getLink()).
                    into(holder.binding.imgVideo);

            int size = position + 1;
            for (int i = 0; i < size; i++) {
                holder.binding.tvStep.setText("Step " + size);
            }

            holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String time = videoinfo.getStart();
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
            return videoData.size();
        }
    }
}


