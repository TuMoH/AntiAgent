package com.gc.materialdesign.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.utils.Utils;

public class ButtonFlat extends ButtonRectangle {
	
	public ButtonFlat(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onInitDefaultValues(){
		textButton = new TextView(getContext());
		minHeight = 36;
        correctMinHeight();
		minWidth = 88;
		rippleSpeed = 15f;
		defaultTextColor =  Color.parseColor("#1E88E5");
		backgroundResId = R.drawable.background_transparent_border;
		rippleColor = Color.parseColor("#88DDDDDD");
		//setBackgroundResource(R.drawable.background_transparent);
	}

        public void correctMinHeight() {
        int screen = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screen == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            minHeight = (int) (minHeight * 1.5);
//        } else if (screen == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
//            minHeight = minHeight * 2;
        } else if (screen == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            minHeight = minHeight * 2;
//        } else if (screen == Configuration.SCREENLAYOUT_SIZE_SMALL) {
//            minHeight = minHeight / 2;
        }
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(Utils.dpToPx(this.minHeight, getResources()));
    }

    @Override
	protected void onDraw(Canvas canvas) {
        if (x != -1) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			if (rippleColor == null) {
				paint.setColor(Color.parseColor("#88DDDDDD"));
			}else {
				paint.setColor(rippleColor);
			}
            canvas.drawCircle(x, y, radius, paint);
            if(radius > getHeight()/rippleSize) {
                radius += rippleSpeed;
                setTextColor(Color.parseColor("#FFFFFFFF"));

            }
            if(radius >= getWidth()){
                x = -1;
				y = -1;
				radius = getHeight()/rippleSize;
				if (isEnabled() && clickAfterRipple && onClickListener != null) {
                    setTextColor(Color.parseColor("#FFFFFFFF"));
                    onClickListener.onClick(this);
                }
            }
		}		
		invalidate();
	}
	
	@Override
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(color);
		if (!settedRippleColor) {
			rippleColor = Color.parseColor("#88DDDDDD");
		}
	}
	
	
}
