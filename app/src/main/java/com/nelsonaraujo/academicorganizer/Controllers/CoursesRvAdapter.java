package com.nelsonaraujo.academicorganizer.Controllers;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.R;

/**
 * Adapter for the courses recycler view.
 */
public class CoursesRvAdapter extends RecyclerView.Adapter<CoursesRvAdapter.CourseViewHolder>{
    private static final String TAG = "CoursesRvAdapter";
    private Cursor mCursor;

    /**
     * Class constructor
     * @param mCursor
     */
    public CoursesRvAdapter(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public CoursesRvAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_rv,parent,false);
        return new CoursesRvAdapter.CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesRvAdapter.CourseViewHolder holder, int position) {
        if( (mCursor == null) || (mCursor.getCount()==0) ){ // If no records are returned
            // Empty no items to display.
        } else { // If records are returned
            if(!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            Course course = new Course(mCursor.getLong(mCursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));


            // Populate RecycleView
            holder.title.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)));
            holder.status.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)));
        }
    }

    @Override
    public int getItemCount() {
        if( (mCursor == null) || (mCursor.getCount()==0) ){
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Swap a new Cursor and return the old cursor.
     * The old Cursor is not closed.
     * @param newCursor The new Cursor.
     * @return The old Cursor. Null if there isn't one or if the previous and new cursors are the same.
     */
    Cursor swapCursor(Cursor newCursor){
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * Inner class ViewHolder.
     */
    static class CourseViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "CourseViewHolder";

        TextView title = null;
        TextView status = null;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.coursesRvCourseTv);
            this.status = (TextView) itemView.findViewById(R.id.coursesRvStatusTv);
        }
    }
}
