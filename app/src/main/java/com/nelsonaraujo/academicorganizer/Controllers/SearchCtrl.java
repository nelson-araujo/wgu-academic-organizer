package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.nelsonaraujo.academicorganizer.R;

public class SearchCtrl extends AppCompatActivity{
    private static final String TAG = "SearchCtrl"; // For terminal logging
    public static final int LOADER_ID = 0; // Loader id to identify the loader if multiple are used.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

    }

    /**
     * Application bar menu.
     * @param menu Menu.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        getSupportActionBar().setTitle("Terms");
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

            case R.id.appbar_search:
                Intent searchIntent = new Intent(this, SearchCtrl.class);
                startActivity(searchIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
