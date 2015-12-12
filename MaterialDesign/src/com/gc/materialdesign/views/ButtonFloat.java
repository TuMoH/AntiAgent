package com.gc.materialdesign.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.utils.Utils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;


public class ButtonFloat extends Button {

    protected int iconSize;
    protected int sizeRadius;

    ImageView icon;
    Drawable iconDrawable;

    public ButtonFloat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInitDefaultValues() {
        super.onInitDefaultValues();
        icon = new ImageView(getContext());
        iconSize = 24;
        sizeRadius = 28;
        rippleSpeed = 6;
        rippleSize = 5;
        minWidth = sizeRadius * 2;// 56dp
        minHeight = sizeRadius * 2;// 56dp
        backgroundResId = R.drawable.background_button_float;
    }

    @Override
    protected void onInitAttributes(AttributeSet attrs) {
        super.onInitAttributes(attrs);

        int iconResource = attrs.getAttributeResourceValue(MATERIALDESIGNXML, "iconDrawable", -1);
        if (iconResource != -1) {
            iconDrawable = getResources().getDrawable(iconResource);
        }

        // animation
        boolean animate = attrs.getAttributeBooleanValue(MATERIALDESIGNXML, "animate", false);
        if (animate) {
            playAnimation();
        }

        if (iconDrawable != null) {
            icon.setBackgroundDrawable(iconDrawable);
        }

        String size = attrs.getAttributeValue(MATERIALDESIGNXML, "iconSize");
        if (size != null) {
            iconSize = (int) Utils.dipOrDpToFloat(size);
        }
        setIconParams();
        addView(icon);
    }

    private void setIconParams() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                Utils.dpToPx(iconSize, getResources()), Utils.dpToPx(iconSize, getResources()));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        icon.setLayoutParams(params);
    }


    private void playAnimation() {
        post(new Runnable() {
            @Override
            public void run() {
                float originalY = ViewHelper.getY(ButtonFloat.this) - Utils.dpToPx(24, getResources());
                ViewHelper.setY(ButtonFloat.this,
                        ViewHelper.getY(ButtonFloat.this) + getHeight() * 3);
                ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonFloat.this, "y", originalY);
                animator.setInterpolator(new OvershootInterpolator());
                animator.setDuration(700);
                animator.start();
            }
        });
    }

    public void playAnimationChange(final Drawable res, final int course) {
        final int time = 500;
        post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setRotation(ButtonFloat.this, 0);
                ObjectAnimator animator = ObjectAnimator.ofFloat(ButtonFloat.this, "rotation", 720 * course);
                animator.setDuration(time);
                animator.start();
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ButtonFloat.this.setIconDrawable(res);
            }
        }, time / 2);
    }

    public void playAnimationSub(final int course) {
        post(new Runnable() {
            @Override
            public void run() {
                if (ButtonFloat.this.getRotation() == 720)
                    ViewHelper.setRotation(ButtonFloat.this, 0);
                ObjectAnimator animator;
                if (ButtonFloat.this.getRotation() == 0)
                    animator = ObjectAnimator.ofFloat(ButtonFloat.this, "rotation", 135 * course);
                else
                    animator = ObjectAnimator.ofFloat(ButtonFloat.this, "rotation", 0);
                animator.setDuration(300);
                animator.start();
            }
        });
    }

    public boolean showSubFab(List<ButtonFloat> subFabList) {
        ButtonFloat.this.playAnimationSub(1);
        for (int i = 0; i < subFabList.size(); i++) {
            final ButtonFloat subFab = subFabList.get(i);
            final int x = i + 1;
            post(new Runnable() {
                @Override
                public void run() {
                    ButtonFloat.this.bringToFront();
                    float originalY = ViewHelper.getY(ButtonFloat.this) - Utils.dpToPx(65 * x, getResources());
                    ViewHelper.setY(subFab, ViewHelper.getY(ButtonFloat.this));
                    subFab.setVisibility(View.VISIBLE);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(subFab, "y", originalY);
                    animator.setInterpolator(new OvershootInterpolator());
                    animator.setDuration(400);
                    animator.start();
                }
            });
        }
        return false;
    }

    public boolean hideSubFab(List<ButtonFloat> subFabList) {
        ButtonFloat.this.playAnimationSub(-1);
        for (final ButtonFloat subFab : subFabList) {
            post(new Runnable() {
                @Override
                public void run() {
                    float originalY = ViewHelper.getY(ButtonFloat.this);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(subFab, "y", originalY);
                    animator.setInterpolator(new AnticipateInterpolator());
                    animator.setDuration(400);
                    animator.start();
                }
            });
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    subFab.setVisibility(View.INVISIBLE);
                }
            }, 400);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {
            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dst = new Rect(Utils.dpToPx(1, getResources()),
                    Utils.dpToPx(2, getResources()),
                    getWidth() - Utils.dpToPx(1, getResources()),
                    getHeight() - Utils.dpToPx(2, getResources()));
            canvas.drawBitmap(cropCircle(makeCircle()), src, dst, null);
        }
        invalidate();
    }


    public Bitmap cropCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    // GET AND SET


    public void isAnimate(boolean isAnimate) {
        if (isAnimate) {
            playAnimation();
        }
    }

    public ImageView getIcon() {
        return icon;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable drawableIcon) {
        this.iconDrawable = drawableIcon;
        icon.setBackgroundDrawable(drawableIcon);
    }


    public void setIconSize(int size) {
        iconSize = size;
        setIconParams();
    }


    public int getIconSize() {
        return iconSize;
    }

    @Override
    @Deprecated
    public TextView getTextView() {

        return null;
    }

}
