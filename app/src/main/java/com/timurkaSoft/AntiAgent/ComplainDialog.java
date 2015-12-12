package com.timurkaSoft.AntiAgent;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gc.materialdesign.views.ButtonFlat;

public class ComplainDialog extends android.app.Dialog {

    Context context;
    View view;
    View backView;
    Spinner spinner;
    EditText etComment;

    private String[] spinnerData = new String[]{"Продано/сдано", "Агент/мошенник!", "Неверная цена", "Неверный адрес", "Не дозвониться"};
    private String[] spinnerDataTag = new String[]{"SOLD_OUT", "IS_AGENT", "WRONG_PRICE", "WRONG_ADDRESS", "PHONE_INACCESSIBLE"};

    ButtonFlat buttonAccept;
    ButtonFlat buttonCancel;

    View.OnClickListener onAcceptButtonClickListener;

    public ComplainDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complain_dialog);

        view = findViewById(R.id.contentDialog);
        backView = findViewById(R.id.dialog_rootView);
        spinner = (Spinner) findViewById(R.id.spinnerType);
        etComment = (EditText) findViewById(R.id.comment);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        backView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < view.getLeft()
                        || event.getX() > view.getRight()
                        || event.getY() > view.getBottom()
                        || event.getY() < view.getTop()) {
                    dismiss();
                }
                return false;
            }
        });

        this.buttonAccept = (ButtonFlat) findViewById(R.id.button_accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAcceptButtonClickListener != null)
                    onAcceptButtonClickListener.onClick(v);
                dismiss();
            }
        });
        this.buttonCancel = (ButtonFlat) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
    }

    public String getType() {
        return spinnerDataTag[spinner.getSelectedItemPosition()];
    }

    public String getComment() {
        return etComment.getText().toString();
    }

    public void setOnAcceptButtonClickListener(
            View.OnClickListener onAcceptButtonClickListener) {
        this.onAcceptButtonClickListener = onAcceptButtonClickListener;
        if (buttonAccept != null)
            buttonAccept.setOnClickListener(onAcceptButtonClickListener);
    }

    @Override
    public void dismiss() {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etComment.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) { /* NOP */ }
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ComplainDialog.super.dismiss();
            }
        });
        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);

        view.startAnimation(anim);
        backView.startAnimation(backAnim);
    }
}
