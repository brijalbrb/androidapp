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

import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import s.com.videoapp.R;
import s.com.videoapp.aws.CrashHandler;
import s.com.videoapp.aws.DatabaseAccess;
import s.com.videoapp.databinding.ActivityHomeBinding;
import s.com.videoapp.databinding.RowHomeBinding;
import s.com.videoapp.pojo.VideoItem;
import s.com.videoapp.utils.StoreUserData;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private Activity activity;
    private DatabaseAccess databaseAccess;
    HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_home);

        setupAWSMobileClient();

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AWSMobileClient.getInstance().initialize(activity, new AWSStartupHandler() {
                    @Override
                    public void onComplete(AWSStartupResult awsStartupResult) {
                        SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(
                                activity,
                                SignInUI.class);
                        signin.login(
                                activity,
                                HomeActivity.class).execute();
                    }
                }).execute();
            }
        });

    }

    private void setupAWSMobileClient() {
        try {
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
            super.onPostExecute(aVoid);
            adapter = new HomeAdapter(activity, aVoid);
            binding.rvHome.setLayoutManager(new GridLayoutManager(activity, 2));
            binding.rvHome.setAdapter(adapter);
            binding.pb.setVisibility(View.GONE);
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
                    load("https://img.youtube.com/vi/" + videoinfo.link.substring(17)+"/hqdefault.jpg").
                    into(holder.binding.imgVideo);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(activity,MainActivity.class)
                            .putExtra("videoID",videoinfo.link)
                            .putExtra("videoTitle",videoinfo.videoTitle).putExtra("taskArray", (Serializable) videoinfo.taskArray));
                }
            });
        }

        @Override
        public int getItemCount() {

            return videoData == null? 0 : videoData.size();

        }
    }

}