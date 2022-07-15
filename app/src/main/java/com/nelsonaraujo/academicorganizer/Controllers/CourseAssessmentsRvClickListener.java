package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CourseAssessmentsRvClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "TermCourseAssessmentsRvClickListener";

    interface OnCourseAssessmentsRvClickListener {
        void onAssessmentClick(View view, int position);
    }

    private final CourseAssessmentsRvClickListener.OnCourseAssessmentsRvClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public CourseAssessmentsRvClickListener(Context context, final RecyclerView recyclerView, CourseAssessmentsRvClickListener.OnCourseAssessmentsRvClickListener listener) {
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
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                Log.d(TAG, "onLongPress: start");
//            }
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
