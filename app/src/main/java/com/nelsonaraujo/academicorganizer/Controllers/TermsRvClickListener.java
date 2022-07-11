package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

// TermsRvClickListener

class TermsRvClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface OnTermsRvClickListener {
//        void onItemClick(View view, int position);
//        void onItemLongClick(View view, int position);
        void onTermClick(View view, int position);
    }

    private final OnTermsRvClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public TermsRvClickListener(Context context, final RecyclerView recyclerView, OnTermsRvClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: start");

                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    Log.d(TAG, "onSingleTapUp: calling listener.onTermClick");
                    mListener.onTermClick(childView,recyclerView.getChildAdapterPosition(childView));
                }


                return true;
            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                Log.d(TAG, "onLongPress: start");
//            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: start"); // todo: remove
        if(mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned: " + result); // todo: remove
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent(): returned: false"); // todo: remove
            return false;
        }
    }
}
