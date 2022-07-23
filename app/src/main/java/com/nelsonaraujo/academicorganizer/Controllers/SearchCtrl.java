package com.nelsonaraujo.academicorganizer.Controllers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;


import com.nelsonaraujo.academicorganizer.Models.Assessment;
import com.nelsonaraujo.academicorganizer.Models.AssessmentContract;
import com.nelsonaraujo.academicorganizer.Models.Course;
import com.nelsonaraujo.academicorganizer.Models.CourseContract;
import com.nelsonaraujo.academicorganizer.Models.SearchItem;
import com.nelsonaraujo.academicorganizer.Models.Term;
import com.nelsonaraujo.academicorganizer.Models.TermContract;
import com.nelsonaraujo.academicorganizer.R;

import java.util.ArrayList;

/**
 * Controller for the search layout.
 */
public class SearchCtrl extends AppCompatActivity{
    private static final String TAG = "SearchCtrl"; // For terminal logging

    private final String TYPE_TERM = "Term";
    private final String TYPE_COURSE = "Course";
    private final String TYPE_ASSESSMENT = "Assessment";

    SearchView searchView;
    ListView listView;
    ArrayAdapter<SearchItem> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        // Create a new search item list
        ArrayList<SearchItem> searchItems = new ArrayList<SearchItem>();

        // Get terms and add to search items list
        ArrayList<Term> terms = getTerms();
        for(Term term : terms){
            SearchItem searchItem = new SearchItem();

            searchItem.setType(TYPE_TERM);
            searchItem.setTitle(term.getTitle());
            searchItem.setId(term.getId());

            searchItems.add(searchItem);
        }

        // Get courses and add to search items list
        ArrayList<Course> courses = getCourses();
        for(Course course : courses){
            SearchItem searchItem = new SearchItem();

            searchItem.setType(TYPE_COURSE);
            searchItem.setTitle(course.getTitle());
            searchItem.setId(course.getId());

            searchItems.add(searchItem);
        }

        // Get assessments and add to search items list
        ArrayList<Assessment> assessments = getAssessments();
        for(Assessment assessment : assessments){
            SearchItem searchItem = new SearchItem();

            searchItem.setType(TYPE_ASSESSMENT);
            searchItem.setTitle(assessment.getTitle());
            searchItem.setId(assessment.getId());

            searchItems.add(searchItem);
        }

        searchView = findViewById(R.id.searchSearchSv);
        listView = findViewById(R.id.searchListViewLv);
        arrayAdapter = new ArrayAdapter<SearchItem>(this,android.R.layout.simple_list_item_1, searchItems);

        // Set the search view active
        searchView.onActionViewExpanded();

        listView.setAdapter(arrayAdapter);
        listView.setVisibility(View.GONE); // Hide list until text is typed in the search field.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchItem itemSelected = (SearchItem) adapterView.getItemAtPosition(i);

                // Open object based on type.
                switch(itemSelected.getType()){
                    case TYPE_TERM:
                        onTermSelection(itemSelected.getId());
                        break;

                    case TYPE_COURSE:
                        onCourseSelection(itemSelected.getId());
                        break;

                    case TYPE_ASSESSMENT:
                        onAssessmentSelection(itemSelected.getId());
                        break;

                    default:
                        throw new IllegalStateException("Search selection type unknown: " + itemSelected.getType());
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchCtrl.this.arrayAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);

                // unhide list
                if(!newText.isEmpty()){
                    listView.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

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

    /**
     * Get a list of all courses.
     * @return list of all course.
     */
    private ArrayList<Course> getCourses(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Query database
        Cursor cursor = contentResolver.query(CourseContract.CONTENT_URI,projection,null,null);

        // Populate array list
        ArrayList<Course> courses = new ArrayList<Course>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Course course = new Course(cursor.getLong(cursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));

                courses.add(course);
            }
        }

        cursor.close();

        return courses;
    }

    /**
     * Get a list of all terms on the system.
     * @return List of terms.
     */
    private ArrayList<Term> getTerms(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID,
                TermContract.Columns.TITLE,
                TermContract.Columns.START,
                TermContract.Columns.END};

        // Query database
        Cursor cursor = contentResolver.query(TermContract.CONTENT_URI,projection,null,null);

        // Populate array list
        ArrayList<Term> terms = new ArrayList<Term>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Term term = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));

                terms.add(term);
            }
        }

        cursor.close();

        return terms;
    }

    /**
     * Get a list of all assessments on the system.
     * @return List of assessments.
     */
    private ArrayList<Assessment> getAssessments(){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        // Query database
        Cursor cursor = contentResolver.query(AssessmentContract.CONTENT_URI,projection,null,null);

        // Populate array list
        ArrayList<Assessment> assessments = new ArrayList<Assessment>();
        if(cursor != null){
            while(cursor.moveToNext()){
                Assessment assessment = new Assessment(cursor.getLong(cursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));

                assessments.add(assessment);
            }
        }

        cursor.close();

        return assessments;
    }

    /**
     * Open the term selected.
     * @param id term id
     */
    public void onTermSelection(long id) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {TermContract.Columns._ID,
                TermContract.Columns.TITLE,
                TermContract.Columns.START,
                TermContract.Columns.END};

        // Get a specific record
        String sortOrder = null; // DB sort order
        String whereSelection = null; // DB Where selection
        Cursor cursor = contentResolver.query(TermContract.buildTermUri(id), projection, whereSelection, null, sortOrder);

        // Get term
        Term selection = new Term(0,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                // Populate selection
                selection = new Term(cursor.getLong(cursor.getColumnIndexOrThrow(TermContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TermContract.Columns.END)));
            }
        }

        // Display selection
        Intent intent = new Intent(this, TermCtrl.class);
        intent.putExtra(Term.class.getSimpleName(), selection);
        startActivity(intent);
    }

    /**
     * Open the course selected.
     * @param id course id
     */
    public void onCourseSelection(long id) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {CourseContract.Columns._ID,
                CourseContract.Columns.TITLE,
                CourseContract.Columns.START,
                CourseContract.Columns.END,
                CourseContract.Columns.STATUS,
                CourseContract.Columns.NOTE,
                CourseContract.Columns.TERM_ID,
                CourseContract.Columns.INSTRUCTOR_ID};

        // Get a specific record
        String sortOrder = null; // DB sort order
        String whereSelection = null; // DB Where selection
        Cursor cursor = contentResolver.query(CourseContract.buildCourseUri(id), projection, whereSelection, null, sortOrder);

        // Get course
        Course selection = new Course(0,null,null,null,null,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                // Populate selection
                selection = new Course(cursor.getLong(cursor.getColumnIndexOrThrow(CourseContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CourseContract.Columns.NOTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.TERM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CourseContract.Columns.INSTRUCTOR_ID)));
            }
        }

        // Display selection
        Intent intent = new Intent(this, CourseCtrl.class);
        intent.putExtra(Course.class.getSimpleName(), selection);
        startActivity(intent);
    }

    /**
     * Open the assessment selected.
     * @param id assessment id
     */
    public void onAssessmentSelection(long id) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Setup projection
        String[] projection = {AssessmentContract.Columns._ID,
                AssessmentContract.Columns.TITLE,
                AssessmentContract.Columns.START,
                AssessmentContract.Columns.END,
                AssessmentContract.Columns.CONTENT,
                AssessmentContract.Columns.COURSE_ID};

        // Get a specific record
        String sortOrder = null; // DB sort order
        String whereSelection = null; // DB Where selection
        Cursor cursor = contentResolver.query(AssessmentContract.buildAssessmentUri(id), projection, whereSelection, null, sortOrder);

        // Get assessment
        Assessment selection = new Assessment(0,null,null,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                // Populate selection
                selection = new Assessment(cursor.getLong(cursor.getColumnIndexOrThrow(AssessmentContract.Columns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(AssessmentContract.Columns.COURSE_ID)));
            }
        }

        // Display selection
        Intent intent = new Intent(this, AssessmentCtrl.class);
        intent.putExtra(Assessment.class.getSimpleName(), selection);
        startActivity(intent);
    }

}
