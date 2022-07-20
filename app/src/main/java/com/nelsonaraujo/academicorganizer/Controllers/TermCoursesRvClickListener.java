package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Click listener for the term courses recycler view.
 */
public class TermCoursesRvClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "TermTermCoursesRvClickListener";
    
    interface OnTermCoursesRvClickListener {
        void onCourseClick(View view, int position);
    }

    private final TermCoursesRvClickListener.OnTermCoursesRvClickListener mListener;
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
     * Term courses recycle view click listener.
     * @param context
     * @param recyclerView
     * @param listener
     */
    public TermCoursesRvClickListener(Context context, final RecyclerView recyclerView, TermCoursesRvClickListener.OnTermCoursesRvClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    mListener.onCourseClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

                return true;
            }
        });

    }
}
