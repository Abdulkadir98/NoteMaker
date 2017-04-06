package com.example.admin.noteapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.admin.noteapp.data.NotesContract.CONTENT_AUTHORITY;
import static com.example.admin.noteapp.data.NotesContract.PATH_NOTES;

/**
 * Created by admin on 1/22/2017.
 */

public class NotesProvider extends ContentProvider {

    private static final int NOTES = 100;
    private static final int NOTES_ID = 101;
    private NoteDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NOTES, NOTES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NOTES + "/#", NOTES_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new NoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(NotesContract.NotesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case NOTES_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = NotesContract.NotesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(NotesContract.NotesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NotesContract.NotesEntry.CONTENT_LIST_TYPE;
            case NOTES_ID:
                return NotesContract.NotesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case NOTES:
                return insertNote(uri, contentValues);
            default:
                throw new IllegalArgumentException("Error in insertion" + uri);
        }
    }
    private Uri insertNote(Uri uri , ContentValues contentValues){

        String noteTitle = contentValues.getAsString(NotesContract.NotesEntry.COLUMN_NOTE_TITLE);
        String note = contentValues.getAsString(NotesContract.NotesEntry.COLUMN_NOTE);
        String noteUrl = contentValues.getAsString(NotesContract.NotesEntry.COLUMN_NOTE_URL);
        if(noteTitle == null){
            throw new IllegalArgumentException("Note needs a title");
        }
        if (note == null){
            throw new IllegalArgumentException("Enter something in the note");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long rowID = database.insert(NotesContract.NotesEntry.TABLE_NAME, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        switch (match){
            case NOTES:
                int rows = database.delete(NotesContract.NotesEntry.TABLE_NAME, selection, selectionArgs);
                return rows;
            case NOTES_ID:
                 selection = NotesContract.NotesEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = database.delete(NotesContract.NotesEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rows;
            default:
                throw new IllegalArgumentException("deletion not supported for"+ uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return updateNote(uri, contentValues, selection, selectionArgs);
            case NOTES_ID:
                selection = NotesContract.NotesEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{(String.valueOf(ContentUris.parseId(uri)))};
                return updateNote(uri,contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Updation not supported" + uri);
        }
    }
    private int updateNote(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs ){
        if(contentValues.size() == 0){
            return 0;
        }
        if(contentValues.containsKey(NotesContract.NotesEntry.COLUMN_NOTE_TITLE)) {
            String noteTitle = contentValues.getAsString(NotesContract.NotesEntry.COLUMN_NOTE_TITLE);
            if (noteTitle == null){
                throw new IllegalArgumentException("Note needs a title");
            }
        }
        if (contentValues.containsKey(NotesContract.NotesEntry.COLUMN_NOTE)) {
            String note = contentValues.getAsString(NotesContract.NotesEntry.COLUMN_NOTE);
            if (note == null) {
                throw new IllegalArgumentException("Enter something in the note");
            }
        }
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            int rows = database.update(NotesContract.NotesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
            return rows;

    }
}
