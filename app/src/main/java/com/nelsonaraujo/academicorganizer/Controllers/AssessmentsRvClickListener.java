package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Click listener for the assessments recycler view.
 */
public class AssessmentsRvClickListener extends RecyclerView.SimpleOnItemTouchListener{
    private static final String TAG = "AssessmentsRvClickListener";

    interface OnAssessmentsRvClickListener {
        void onAssessmentClick(View view, int position);
    }

    private final AssessmentsRvClickListener.OnAssessmentsRvClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public AssessmentsRvClickListener(Context context, final RecyclerView recyclerView, AssessmentsRvClickListener.OnAssessmentsRvClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    mListener.onAssessmentClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

                return true;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if(mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            return result;
        } else {
            return false;
        }
    }
}
