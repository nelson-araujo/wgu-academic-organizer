package com.nelsonaraujo.academicorganizer.Models;

import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY;
import static com.nelsonaraujo.academicorganizer.Models.AppProvider.CONTENT_AUTHORITY_URI;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Instructor database structure details and content provider URI manipulation.
 */
public class InstructorContract {
    static final String TABLE_NAME = "instructor";

    // Instructor fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";


        private Columns(){
            // private constructor to prevent instantiation.
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Build instructor uri with the id appended.
     * @param instructorId instructor id to append.
     * @return uri with appended id.
     */
    public static Uri buildInstructorUri(long instructorId){
        return ContentUris.withAppendedId(CONTENT_URI, instructorId);
    }

    /**
     * Get the instructor id from the uri.
     * @param uri uri with the id appended.
     * @return id
     */
    static long getInstructorId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
