package com.gc.materialdesign.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gc.materialdesign.R;

/**
 * Created by TuMoH on 01.03.2015.
 */
public class ButtonFlatUnmodified extends ButtonFlat {

    public ButtonFlatUnmodified(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInitDefaultValues(){
        textButton = new TextView(getContext());
        minHeight = 36;
        minWidth = 88;
        rippleSpeed = 15f;
        defaultTextColor =  Color.parseColor("#1E88E5");
        backgroundResId = R.drawable.background_transparent;
        rippleColor = Color.parseColor("#88DDDDDD");
        //setBackgroundResource(R.drawable.background_transparent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (x != -1) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#58585858"));
            canvas.drawCircle(x, y, radius, paint);
            if(radius > getHeight()/rippleSize) {
                radius += rippleSpeed;
            }
            if(radius >= getWidth()){
                x = -1;
                y = -1;
                radius = getHeight()/rippleSize;
                if (isEnabled() && clickAfterRipple && onClickListener != null) {
                    onClickListener.onClick(this);
                }
            }
        }
        invalidate();
    }

}
