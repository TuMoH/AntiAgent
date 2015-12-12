package com.timurkaSoft.AntiAgent.photos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentInfo;
import com.timurkaSoft.AntiAgent.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class ActPhotos extends Activity implements ViewPager.OnPageChangeListener {

    DisplayImageOptions options;
    TextView photoCounter;
    CustomViewPager pager;
    ImageAdapter adapter;
    int rotation = 0;
    private List<String> img = HeadFragmentInfo.moreInfo.getImg();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photos);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_menu_camera_off)
                .showImageOnFail(R.drawable.ic_menu_camera_off)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        adapter = new ImageAdapter();
        pager = (CustomViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);

        photoCounter = (TextView) findViewById(R.id.photoCounter);
        photoCounter.setText("1" + "/" + img.size());

        int startPosition = getIntent().getIntExtra("img_position", 0);
        pager.setCurrentItem(startPosition);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean("isLocked", false);
            pager.setLocked(isLocked);
        }
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void toggleRotation(final View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rotation < 90) {
                    rotation++;
                    try {
                        adapter.getPhoto().setRotationBy(1);
                        toggleRotation(view);
                    } catch (Exception e) { /*NOP*/ }
                } else {
                    rotation = 0;
                }
            }
        }, 15);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean("isLocked", pager.isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    private boolean isViewPagerActive() {
        return (pager != null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        photoCounter.setText((position + 1) + "/" + img.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class ImageAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private View currentView;
        private ViewGroup viewGroup;

        ImageAdapter() {
            inflater = LayoutInflater.from(ActPhotos.this);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return img.size();
        }

        public PhotoView getPhoto() {
            String[] counts = photoCounter.getText().toString().split("/");
            if (counts[1].equals(counts[0]))
                currentView = viewGroup.getChildAt(0);
            return currentView != null ? (PhotoView) currentView.findViewById(R.id.photo) : null;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            PhotoView photo = (PhotoView) imageLayout.findViewById(R.id.photo);
            final ProgressWheel spinner = (ProgressWheel) imageLayout.findViewById(R.id.loading);

            ImageLoader.getInstance().displayImage(img.get(position), photo, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            String[] counts = photoCounter.getText().toString().split("/");
            if ("1".equals(counts[0]))
                currentView = view.getChildAt(view.getChildCount() - 1);
            else if (view.getChildCount() > 1)
                currentView = view.getChildAt(1);
            viewGroup = view;
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
