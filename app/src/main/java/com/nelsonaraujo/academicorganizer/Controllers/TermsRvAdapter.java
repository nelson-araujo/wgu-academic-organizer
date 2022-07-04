package com.nelsonaraujo.academicorganizer.Controllers;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

class TermsRvAdapter extends RecyclerView.Adapter<TermsRvAdapter.TermViewHolder> {
    private static final String TAG = "TermsRvAdapter";
    private Cursor mCursor;

    /**
     * Constructor for TermsRvAdapter
     */
    public TermsRvAdapter(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested"); // todo: remove
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.terms_list_terms,parent,false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: providing instructions"); // todo: remove

        if( (mCursor == null) || (mCursor.getCount()==0) ){ // If no records are returned
            holder.title.setText("No terms found");
        } else { // If records are returned
            if(!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            holder.title.setText(mCursor.getString(mCursor.getColumnIndex(TermContract.Columns.TERM_TITLE)));
            holder.start.setText(mCursor.getString(mCursor.getColumnIndex(TermContract.Columns.TERM_START)));
            holder.end.setText(mCursor.getString(mCursor.getColumnIndex(TermContract.Columns.TERM_END)));
        }
    }

    @Override
    public int getItemCount() {
        if( (mCursor == null) || (mCursor.getCount()==0) ){
            return 1; // return one row to display the "No terms found" message.
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
            notifyItemRangeChanged(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * Inner class ViewHolder.
     */
    static class TermViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TermViewHolder";

        TextView title = null;
        TextView start = null;
        TextView end = null;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.tltNameTv);
            this.start = (TextView) itemView.findViewById(R.id.tltStartTv);
            this.end = (TextView) itemView.findViewById(R.id.tltEndTv);
        }
    }
}