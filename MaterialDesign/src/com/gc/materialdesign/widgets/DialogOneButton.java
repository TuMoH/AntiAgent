package com.gc.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.views.ButtonFlat;

public class DialogOneButton extends Dialog{

	public DialogOneButton(Context context, String title, String message) {
		super(context, title, message);
	}
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    findViewById(R.id.button_cancel).setVisibility(View.GONE);
        ((ButtonFlat)findViewById(R.id.button_accept)).setText("Закрыть");
	}

}
