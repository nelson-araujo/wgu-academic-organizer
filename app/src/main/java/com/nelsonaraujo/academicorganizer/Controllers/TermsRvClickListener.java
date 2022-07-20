package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Terms recycle view click listener.
 */
class TermsRvClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface OnTermsRvClickListener {
        void onTermClick(View view, int position);
    }

    private final OnTermsRvClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if(mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            return result;
        } else {
            return false;
        }
    }

    /**
     * Terms recycle view listener.
     * @param context
     * @param recyclerView
     * @param listener
     */
    public TermsRvClickListener(Context context, final RecyclerView recyclerView, OnTermsRvClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    mListener.onTermClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

                return true;
            }
        });

    }
}