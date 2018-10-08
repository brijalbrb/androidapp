package s.com.videoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import s.com.videoapp.R;
import s.com.videoapp.aws.AmazonClientManager;
import s.com.videoapp.aws.CrashHandler;
import s.com.videoapp.aws.DatabaseAccess;
import s.com.videoapp.aws.DynamoDBManager;
import s.com.videoapp.databinding.ActivityHomeBinding;
import s.com.videoapp.databinding.RowHomeBinding;
import s.com.videoapp.pojo.VideoItem;
import s.com.videoapp.utils.StoreUserData;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private Activity activity;
    private DatabaseAccess databaseAccess;
    HomeAdapter adapter;
    private AmazonClientManager amazonClientManager;
    private DynamoDBManager dynamoDBManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_home);

        setupAWSMobileClient();

    }

    private void setupAWSMobileClient() {
        try {
            amazonClientManager = new AmazonClientManager(activity);
            dynamoDBManager = new DynamoDBManager(activity);
            CrashHandler.installHandler(this);
            new GetDataTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class GetDataTask extends AsyncTask<Void, Void, List<VideoItem>> {

        @Override
        protected List<VideoItem> doInBackground(Void... voids) {
            List<VideoItem> documentList = null;
            try {

                databaseAccess = DatabaseAccess.getInstance(activity);
                documentList = databaseAccess.getAllVideos();

                Log.d("TestData", "Data : " + documentList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return documentList;
        }


        @Override
        protected void onPostExecute(List<VideoItem> aVoid) {

            adapter = new HomeAdapter(activity, aVoid);
            binding.rvHome.setLayoutManager(new GridLayoutManager(activity, 2));
            binding.rvHome.setAdapter(adapter);
            binding.pb.setVisibility(View.GONE);


            super.onPostExecute(aVoid);
        }
    }


    public class HomeAdapter extends RecyclerView.Adapter<HomeActivity.HomeAdapter.MyViewHolder> {

        private List<VideoItem> videoData;
        Activity activity;
        StoreUserData storeUserData;

        public HomeAdapter(ArrayList<VideoItem> items) {
            super();
            storeUserData = new StoreUserData(activity);
            this.videoData = items;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            RowHomeBinding binding;

            public MyViewHolder(View view) {
                super(view);
                binding = DataBindingUtil.bind(view);
            }
        }


        public HomeAdapter(Activity activity, List<VideoItem> videoData) {
            this.videoData = videoData;
            this.activity = activity;
        }

        @Override
        public HomeActivity.HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_home, parent, false);
            return new HomeActivity.HomeAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HomeActivity.HomeAdapter.MyViewHolder holder, final int position) {
            final VideoItem videoinfo = videoData.get(position);


            holder.binding.tvCategoryTitle.setText(videoinfo.videoTitle);
            holder.binding.tvTitle.setText(String.valueOf(videoinfo.year));

            Glide.with(activity).
                    load(videoinfo.videoThumbnail).
                    into(holder.binding.imgVideo);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity,MainActivity.class));
                }
            });

        }

        @Override
        public int getItemCount() {
            return videoData.size();
        }
    }

}