package com.nelsonaraujo.academicorganizer.Models;

import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY;
import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class CourseContract {
    static final String TABLE_NAME = "course";

    // course fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START = "start";
        public static final String END = "end";
        public static final String STATUS = "status";
        public static final String NOTE = "note";
        public static final String TERM_ID = "term_id";
        public static final String INSTRUCTOR_ID = "instructor_id";


        private Columns(){
            // private constructor to prevent instantiation.
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildCourseUri(long courseId){
        return ContentUris.withAppendedId(CONTENT_URI, courseId);
    }

    static long getCourseId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
