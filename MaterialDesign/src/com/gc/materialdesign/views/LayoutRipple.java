package com.gc.materialdesign.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

public class LayoutRipple extends RippleView {

	private Float xRippleOrigin;
	private Float yRippleOrigin;
	float rippleBorderRadius = 0;// radius
	
	public LayoutRipple(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(attrs);
	}
	
	@Override
	protected void onInitDefaultValues() {
		minWidth = 20;
		minHeight = 20;
		backgroundColor = 0x00ffffff;// alpha = 0
		rippleSpeed = 20f;// default speed
	}
	
	@Override
	protected void setAttributes(AttributeSet attrs) {
		super.setAttributes(attrs);
		rippleBorderRadius = attrs.getAttributeFloatValue(MATERIALDESIGNXML, "rippleBorderRadius", 0);
	}
	
	// ### RIPPLE EFFECT ###
	
	public Bitmap makeCircle() {
		Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		x = (xRippleOrigin == null) ? x : xRippleOrigin;
		y = (yRippleOrigin == null) ? y : yRippleOrigin;
		return makeCircleFromBitmap(output);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {
			Rect src = new Rect(0, 0, getWidth(), getHeight());
			Rect dst = new Rect(0, 0, getWidth(), getHeight());
			canvas.drawBitmap(cropRoundRect(makeCircle()), src, dst, null);
		}
		invalidate();
	}
	

	public Bitmap cropRoundRect(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	    bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    canvas.drawRoundRect(rectF, rippleBorderRadius, rippleBorderRadius, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	}

	
	
	// SETTERS
	
	public void setxRippleOrigin(Float xRippleOrigin) {
		this.xRippleOrigin = xRippleOrigin;
	}

	public void setyRippleOrigin(Float yRippleOrigin) {
		this.yRippleOrigin = yRippleOrigin;
	}
	
	/**
	 * set the layoutRipple border radius
	 * @param size
	 */
	public void setRippleBorderRadius(float size) {
		rippleBorderRadius = size;
	}
	
	// Set color of background
	public void setBackgroundColor(int color){
		backgroundColor = color;

		if (!settedRippleColor) {
			rippleColor = makePressColor(255);
		}
		super.setBackgroundColor(color);
	}
	
}
