package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Click listener for the course assessments recycler view.
 */
public class CourseAssessmentsRvClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "TermCourseAssessmentsRvClickListener";

    interface OnCourseAssessmentsRvClickListener {
        void onAssessmentClick(View view, int position);
    }

    private final CourseAssessmentsRvClickListener.OnCourseAssessmentsRvClickListener mListener;
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
     * Course assessment recycler view click listener.
     * @param context
     * @param recyclerView
     * @param listener
     */
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
        });

    }
}
