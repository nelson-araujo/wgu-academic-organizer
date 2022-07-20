package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

/**
 * Controller for the terms layout.
 */
public class TermsCtrl extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TermsRvClickListener.OnTermsRvClickListener {
    private static final String TAG = "Terms"; // For terminal logging
    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private TermsRvAdapter mAdapter; // adapter reference
    private String sortOrder = TermContract.Columns.TITLE; // DB sort order
    private String whereSelection = null; // DB Where selection

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
                addTerm(null); // call addTerm and pass null as we want to create a new term.
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = { TermContract.Columns._ID, TermContract.Columns.TITLE,
                                TermContract.Columns.START, TermContract.Columns.END};

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, TermContract.CONTENT_URI,projection,whereSelection,null, sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onTermClick(View view, int position) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID, TermContract.Columns.TITLE, TermContract.Columns.START, TermContract.Columns.END};

        // Get a specific record
        long recordId = getRecordId(position);
        Cursor cursor = contentResolver.query(TermContract.buildTermUri(recordId), projection, null, null, TermContract.Columns.TITLE);

        // Get term
        Term selectedTerm = new Term(0,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                // Populate term
                selectedTerm = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));
            }
            cursor.close();
        }

        // Display term
        Intent termIntent = new Intent(TermsCtrl.this, TermCtrl.class);
        termIntent.putExtra(Term.class.getSimpleName(), selectedTerm);
        startActivity(termIntent);
    }

    /**
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Application bar menu item selection.
     * @param item Item selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.appbar_terms:
                Intent termsIntent = new Intent(this, TermsCtrl.class);
                startActivity(termsIntent);
                break;

            case R.id.appbar_courses:
                Intent coursesIntent = new Intent(this, CoursesCtrl.class);
                startActivity(coursesIntent);
                break;

            case R.id.appbar_assessments:
                Intent assessmentsIntent = new Intent(this, AssessmentsCtrl.class);
                startActivity(assessmentsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add a term. Function accepts a term for editing, if one is provided it edit the term otherwise it adds.
     * @param term to edit.
     */
    public void addTerm(Term term){
        Intent addTermIntent = new Intent(TermsCtrl.this, TermAddEditCtrl.class);
        if(term != null) { // editing a term
            addTermIntent.putExtra(Term.class.getSimpleName(), term);
            startActivity(addTermIntent);
        } else { // Adding a new term
            startActivity(addTermIntent);
        }
    }

    /**
     * Calculate the actual id of the row selected.
     * @param position RV position tapped.
     * @return id number of entry.
     */
    private long getRecordId(int position){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        String[] projection = {TermContract.Columns._ID};
        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI, projection, whereSelection, null, sortOrder);

        if( (cursor == null) || (cursor.getCount()==0) ){ // If no records are returned
            return -1; // Unable to determine position
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Unable to move cursor to position " + position);
            }

            // move to position
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID));
        }
    }
}