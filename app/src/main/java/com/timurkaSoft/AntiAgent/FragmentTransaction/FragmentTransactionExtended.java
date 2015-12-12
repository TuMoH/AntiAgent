package com.timurkaSoft.AntiAgent.FragmentTransaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import com.timurkaSoft.AntiAgent.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentTransactionExtended implements FragmentManager.OnBackStackChangedListener {
    private boolean mDidSlideOut = false;
    private boolean mIsAnimating = false;
    private FragmentTransaction mFragmentTransaction;
    private Context mContext;
    private Fragment mSecondFragment;
    private int mContainerID;
    private boolean addToBackStack = true;

    public FragmentTransactionExtended(Context context, FragmentTransaction fragmentTransaction, Fragment secondFragment, int containerID) {
        this.mFragmentTransaction = fragmentTransaction;
        this.mContext = context;
        this.mSecondFragment = secondFragment;
        this.mContainerID = containerID;
    }

    public void addToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
    }

    public void sliding() {
        ((Activity) this.mContext).getFragmentManager().addOnBackStackChangedListener(this);

        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            ((Activity) mContext).getFragmentManager().popBackStack();
        } else {
            mDidSlideOut = true;
            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    mFragmentTransaction.setCustomAnimations(R.animator.slide_fragment_in, 0, 0, R.animator.slide_fragment_out);
                    mFragmentTransaction.add(mContainerID, mSecondFragment);
                    if (addToBackStack)
                        mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                }
            };
            AnimatorSet s = new AnimatorSet();
            s.addListener(listener);
            s.start();
        }
    }

    @Override
    public void onBackStackChanged() {
        if (mDidSlideOut) {
            mDidSlideOut = false;
        }
    }
}
