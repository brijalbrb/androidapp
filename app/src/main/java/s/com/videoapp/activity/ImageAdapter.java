package s.com.videoapp.activity;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import s.com.videoapp.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    Bitmap bitmappic;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSnapPic;

        public MyViewHolder(View view) {
            super(view);
            ivSnapPic = (ImageView) view.findViewById(R.id.ivSnapss);
        }
    }


    public ImageAdapter(Bitmap bitmap) {
        this.bitmappic = bitmap;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ivSnapPic.setImageBitmap(bitmappic);
    }

    @Override
    public int getItemCount() {
        return 0;

    }
}