package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.security.InvalidParameterException;

public class Terms extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "Terms"; // For terminal logging
    FloatingActionButton menuBtn;

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private TermsRvAdapter mAdapter; // adapter reference

    // Constructor
    public Terms(){
        Log.d(TAG, "Terms: starts"); // todo: remove

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        // Create adapter and pass data
        mAdapter = new TermsRvAdapter(null);
        RecyclerView termsRv = findViewById(R.id.termsTermRv);
        termsRv.setHasFixedSize(true);
        termsRv.setLayoutManager(new LinearLayoutManager(this));
        termsRv.setAdapter(mAdapter);

        // Add floating action button
        FloatingActionButton addFab = findViewById(R.id.termsAddFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ADD TERM SELECTED"); // todo: remove
                addTerm(null); // call addTerm and pass null as we want to create a new term.
            }
        });


        // ----------------------------------------------------------------------------------------
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();


        // ADD RECORD // todo: remove
//        int termIdToUpdate = 15;
//        String termName = "Term " + termIdToUpdate;
//        Log.d(TAG, "onCreate: Add " + termName);
//        ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, termName);
//            values.put(TermContract.Columns.TERM_START, "20" + termIdToUpdate + "-01-01");
//            values.put(TermContract.Columns.TERM_END,"20" + (termIdToUpdate+1) + "-01-01");
//        Uri uri = contentResolver.insert(TermContract.CONTENT_URI, values);

        // UPDATE RECORD // todo: remove
//        int termIdToUpdate = 1;
//        // String newTermName = "Term 00";
//        String newTermName = "Term " + termIdToUpdate;
//        Log.d(TAG, "onCreate: Update " + newTermName);
//        ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, newTermName);
//            values.put(TermContract.Columns.TERM_START, "20" + termIdToUpdate + "-01-01");
//            values.put(TermContract.Columns.TERM_END,"20" + (termIdToUpdate+1) + "-01-01");
//        int recordsUpdated = contentResolver.update(TermContract.buildTermUri(termIdToUpdate), values,null,null);
//        Log.d(TAG, "onCreate: Total records updated: " + recordsUpdated);

        // UPDATE MULTIPLE RECORDS // todo: remove
//         ContentValues values = new ContentValues();
//            values.put(TermContract.Columns.TERM_TITLE, "Term 99");
//        String selection = TermContract.Columns.TERM_TITLE + "=" + "'Term 00'";
//        int recordsUpdated = contentResolver.update(TermContract.CONTENT_URI, values,selection,null);
//        Log.d(TAG, "onCreate: Total records updated: " + recordsUpdated);

        // DELETE RECORD // todo, remove
//        int recordsDeleted = contentResolver.delete(TermContract.buildTermUri(9),null,null);
//        Log.d(TAG, "onCreate: Records deleted: " + recordsDeleted);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuTerms:
                Log.d(TAG, "onContextItemSelected: TERMS SELECTED"); // todo: remove
                return true;

            case R.id.menuCourses:
                Log.d(TAG, "onContextItemSelected: COURSES SELECTED"); // todo: remove
                return true;

            case R.id.menuAssessments:
                Log.d(TAG, "onContextItemSelected: ASSESSMENTS SELECTED"); // todo: remove
                return true;

            case R.id.termsAddFab:
                Log.d(TAG, "onContextItemSelected: ADD TERM SELECTED"); // todo: remove
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addTerm(Term term){
        Log.d(TAG, "addTerm: Start"); // todo: remove
        Intent addTermIntent = new Intent(Terms.this, TermAddEdit.class);
        if(term != null) { // editing a term
            addTermIntent.putExtra(Term.class.getSimpleName(), term);
            startActivity(addTermIntent);
        } else { // Adding a new term
            startActivity(addTermIntent);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: started with id: " + id);

        String[] projection = { TermContract.Columns._ID, TermContract.Columns.TERM_TITLE,
                                TermContract.Columns.TERM_START, TermContract.Columns.TERM_END};

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, TermContract.CONTENT_URI,projection,null,null,null);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished: "); // todo: remove
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: Count is: " + count); // todo: remove
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: "); // todo: remove
        mAdapter.swapCursor(null);
    }
}