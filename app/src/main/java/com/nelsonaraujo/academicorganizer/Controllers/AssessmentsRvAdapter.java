package com.nelsonaraujo.academicorganizer.Controllers;

import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.R;

/**
 * Adapter for the assessments recycler view.
 */
public class AssessmentsRvAdapter extends RecyclerView.Adapter<AssessmentsRvAdapter.AssessmentViewHolder> {
    private static final String TAG = "AssessmentsRvAdapter";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Cursor mCursor;

    /**
     * Class constructor
     * @param mCursor
     */
    public AssessmentsRvAdapter(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public AssessmentsRvAdapter.AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessments_rv,parent,false);
        return new AssessmentsRvAdapter.AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentsRvAdapter.AssessmentViewHolder holder, int position) {
        if( (mCursor == null) || (mCursor.getCount()==0) ){ // If no records are returned
            // Empty no items to list
        } else { // If records are returned
            if(!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            Assessment assessment = new Assessment(mCursor.getLong(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));

            // Populate RecycleView
            holder.title.setText(assessment.getTitle());
            holder.end.setText(assessment.getEnd());
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
    static class AssessmentViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "AssessmentViewHolder";

        TextView title = null;
        TextView end = null;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.assessmentsRvAssessmentTv);
            this.end = (TextView) itemView.findViewById(R.id.assessmentsRvDueDateTv);
        }
    }
}
