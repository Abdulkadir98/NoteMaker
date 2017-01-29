package com.example.admin.noteapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 1/21/2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

    public NoteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TEXT_TYPE = " TEXT";
        final String COMMA_SEP = ",";
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + NotesContract.NotesEntry.TABLE_NAME + " ("  +
                NotesContract.NotesEntry.COLUMN_ID + " " + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                NotesContract.NotesEntry.COLUMN_NOTE_TITLE + TEXT_TYPE + COMMA_SEP + NotesContract.NotesEntry.COLUMN_NOTE +
                " )";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
