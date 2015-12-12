package com.timurkaSoft.AntiAgent.photos;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.timurkaSoft.AntiAgent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuMoH on 20.06.2015.
 */
public class ImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DisplayImageOptions options;

    List<String> urls = new ArrayList<>();

    public ImageRecyclerAdapter() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_menu_camera_off)
                .showImageOnFail(R.drawable.ic_menu_camera_off)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ImageView image = ((ViewHolder) viewHolder).image;
        final ProgressWheel progress = ((ViewHolder) viewHolder).progress;
        ImageLoader.getInstance().displayImage(urls.get(position), image, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ProgressWheel progress;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.photo_item_img);
            progress = (ProgressWheel) itemView.findViewById(R.id.photo_item_loading);
        }
    }
}
