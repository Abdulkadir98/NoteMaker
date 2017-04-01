package com.example.admin.noteapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by admin on 1/21/2017.
 */

public final class NotesContract {
    private NotesContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.admin.noteapp";
    public static final String PATH_NOTES = "notes";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

 public static class NotesEntry implements BaseColumns{
     public static final String CONTENT_LIST_TYPE =
             ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

     /**
      * The MIME type of the {@link #CONTENT_URI} for a single pet.
      */
     public static final String CONTENT_ITEM_TYPE =
             ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;
     public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);
     public static final String TABLE_NAME = "Notes";
     public static final String COLUMN_ID = BaseColumns._ID;
     public static final String COLUMN_NOTE_TITLE = "Title";
     public static final String COLUMN_NOTE = "Note";
     public static final String COLUMN_NOTE_URL = "Url";

 }

}

