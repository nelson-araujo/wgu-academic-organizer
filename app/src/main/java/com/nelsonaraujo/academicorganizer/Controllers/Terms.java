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

    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    private TermsRvAdapter mAdapter; // adapter reference

    // Constructor
    public Terms(){

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
                addTerm(null); // call addTerm and pass null as we want to create a new term.
            }
        });
    }

    public void addTerm(Term term){
        Intent addTermIntent = new Intent(Terms.this, TermAddEditCtrl.class);
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
        String[] projection = { TermContract.Columns._ID, TermContract.Columns.TITLE,
                                TermContract.Columns.START, TermContract.Columns.END};

        String sortOrder = null; // Set sort order

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
        Cursor cursor = contentResolver.query(TermContract.buildTermUri(position+1), projection, null, null, TermContract.Columns.TITLE);

        // Get term
        Term selectedTerm = new Term(0,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){ // todo: Why does assigning to selectedTerm return a -1 when outside loop? -1 mean column not found.
                // Populate term
                selectedTerm = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));
            }
            cursor.close();
        }

        // Display term
        Intent termIntent = new Intent(Terms.this, TermCtrl.class);
        termIntent.putExtra(Term.class.getSimpleName(), selectedTerm);
        startActivity(termIntent);
    }
}