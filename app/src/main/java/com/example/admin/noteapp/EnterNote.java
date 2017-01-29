package com.example.admin.noteapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.noteapp.data.NotesContract;

public class EnterNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mEnterNote;
    private EditText mEnterNoteTitle;
    private Uri mUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_note);
        Intent intent = getIntent();
         mUri = intent.getData();
        if (mUri == null){
            setTitle("Add a note");
        }
        else{
            setTitle("Edit note");
            getLoaderManager().initLoader(1,null,this);

        }

    }
    private void saveNote(){
        mEnterNoteTitle = (EditText)findViewById(R.id.enter_note_title);
        mEnterNote = (EditText)findViewById(R.id.enter_note);
String noteTitleString = mEnterNoteTitle.getText().toString();
        String noteString = mEnterNote.getText().toString();
        ContentValues values = new ContentValues();
        values.put(NotesContract.NotesEntry.COLUMN_NOTE_TITLE,noteTitleString);
        values.put(NotesContract.NotesEntry.COLUMN_NOTE,noteString);

        if(mUri == null) {
            mUri = getContentResolver().insert(NotesContract.NotesEntry.CONTENT_URI, values);

            if (mUri == null) {
                Toast.makeText(this, "Error in saving note.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int rowsUpdated = getContentResolver().update(mUri, values, null,null);
            if (rowsUpdated == 0){
                Toast.makeText(this,"Error in updating note", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Updated note", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save data to database
                saveNote();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                // Do nothing for now

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] Projection = {NotesContract.NotesEntry.COLUMN_ID, NotesContract.NotesEntry.COLUMN_NOTE_TITLE,
                NotesContract.NotesEntry.COLUMN_NOTE};
        return new CursorLoader(getApplicationContext(), mUri, Projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            String noteTitle = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE));
            mEnterNoteTitle = (EditText) findViewById(R.id.enter_note_title);
            mEnterNote = (EditText) findViewById(R.id.enter_note);

            mEnterNoteTitle.setText(noteTitle);
            mEnterNote.setText(note);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEnterNote.setText("");
        mEnterNoteTitle.setText("");

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method
        int row = getContentResolver().delete(mUri, null, null);
        if (row == 0){
            Toast.makeText(this, "error in deleting note.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Note deleted successfully",Toast.LENGTH_SHORT).show();
        }

    }
}
