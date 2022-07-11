package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
//import android.view.MenuItem;
import android.view.View;

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

public class Terms extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TermsRvClickListener.OnTermsRvClickListener {
    private static final String TAG = "Terms"; // For terminal logging
//    FloatingActionButton menuBtn;

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
        RecyclerView termsRv = (RecyclerView) findViewById(R.id.termsTermRv);
        termsRv.setHasFixedSize(true);
        termsRv.setLayoutManager(new LinearLayoutManager(this));
        termsRv.addOnItemTouchListener(new TermsRvClickListener(this, termsRv, this));
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

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.menuTerms:
//                Log.d(TAG, "onContextItemSelected: TERMS SELECTED"); // todo: remove
//                return true;
//
//            case R.id.menuCourses:
//                Log.d(TAG, "onContextItemSelected: COURSES SELECTED"); // todo: remove
//                return true;
//
//            case R.id.menuAssessments:
//                Log.d(TAG, "onContextItemSelected: ASSESSMENTS SELECTED"); // todo: remove
//                return true;
//
//            case R.id.termsAddFab:
//                Log.d(TAG, "onContextItemSelected: ADD TERM SELECTED"); // todo: remove
//                return true;
//
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    public void addTerm(Term term){
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

        // Sort order by length to sort term 1, term 2, and term 10 properly and no case sensitive.
//        String sortOrder = "LENGTH(" + TermContract.Columns.TERM_TITLE + ")" + ", " + TermContract.Columns.TERM_TITLE + " COLLATE NOCASE";
        String sortOrder = null; // todo sort order

        switch (id) {
            case LOADER_ID:
                ContentResolver contentResolver = getContentResolver();
                return new CursorLoader(this, TermContract.CONTENT_URI,projection,null,null, sortOrder);

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

    @Override
    public void onTermClick(View view, int position) {
        Log.d(TAG, "onTermClick: at position " + position);

        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID, TermContract.Columns.TERM_TITLE, TermContract.Columns.TERM_START, TermContract.Columns.TERM_END};

        // Get all data // todo: remove
//        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI, projection, null, null, TermContract.Columns.TERM_TITLE);

        // Get a specific record
        Cursor cursor = contentResolver.query(TermContract.buildTermUri(position+1), projection, null, null, TermContract.Columns.TERM_TITLE);

        // Get term
        Term selectedTerm = new Term(0,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                // Populate term
                selectedTerm = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TERM_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TERM_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TERM_END)));

//                for(int i=0 ; i<cursor.getColumnCount() ; i++){ // todo: remove
//                    Log.d(TAG, "onTermClick: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
//                }
            }
            cursor.close();
        }

        // Display term
        Intent termIntent = new Intent(Terms.this, TermCtrl.class);
        termIntent.putExtra(Term.class.getSimpleName(), selectedTerm);
        startActivity(termIntent);

//        Cursor cursor = contentResolver.query(TermContract.buildTermUri(0));

//        String selectedTerm = contentResolver.getType(TermContract.buildTermUri(0));
//        Log.d(TAG, "onTermClick: selectedTerm:" + selectedTerm);
    }
}