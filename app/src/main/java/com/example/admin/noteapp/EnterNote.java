package com.example.admin.noteapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.noteapp.data.NotesContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnterNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mEnterNote;
    private EditText mEnterNoteTitle;
    private ImageView imageView;
    private Uri mUri, mImageUri;
    private boolean mNoteHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };
    private String noteTitleString, noteString;
    static final int IMAGE_CAPTURE = 101;
    private String mCurrentPhotoPath;
    private static final String TAG = EnterNote.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_note);
        mEnterNoteTitle = (EditText)findViewById(R.id.enter_note_title);
        mEnterNote = (EditText)findViewById(R.id.enter_note);
        mEnterNoteTitle.setOnTouchListener(mTouchListener);
        mEnterNote.setOnTouchListener(mTouchListener);
        Intent intent = getIntent();
         mUri = intent.getData();
        if (mUri == null){
            setTitle("Add a note");

        }
        else{
            setTitle("Edit note");
            getLoaderManager().initLoader(1,null,this);
//            imageView = (ImageView)findViewById(R.id.note_image);
//
//                String projection[] = {NotesContract.NotesEntry.COLUMN_NOTE_URL};
//                Cursor cursor = getContentResolver().query(mUri, projection,null,null,null);
//                mImageUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE_URL)));
//                Picasso.with(getApplicationContext()).load(mImageUri).into(imageView);

        }



    }
    private void saveNote(){
        mEnterNoteTitle = (EditText)findViewById(R.id.enter_note_title);
        mEnterNote = (EditText)findViewById(R.id.enter_note);
        noteTitleString = mEnterNoteTitle.getText().toString();
        noteString = mEnterNote.getText().toString();
        // contentValues object for notes table
        ContentValues values1 = new ContentValues();
        values1.put(NotesContract.NotesEntry.COLUMN_NOTE_TITLE,noteTitleString);
        values1.put(NotesContract.NotesEntry.COLUMN_NOTE,noteString);
        //content values object for images table
        ContentValues values2 = new ContentValues();
        if(mCurrentPhotoPath!=null) {
//            String[] projection = {NotesContract.NotesEntry.COLUMN_ID};
//            Cursor cursor = getContentResolver().query(mUri, projection,null,null,null);
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_ID));

            values2.put(NotesContract.ImagesEntry.COLUMN_IMAGE_URL, mCurrentPhotoPath);
//            values2.put(NotesContract.ImagesEntry.COLUMN_IMAGE_ID,id);
        }

        if(mUri == null) {
            mUri = getContentResolver().insert(NotesContract.NotesEntry.CONTENT_URI, values1);
            mUri = getContentResolver().insert(NotesContract.ImagesEntry.CONTENT_URI_IMAGES, values2);


            if (mUri == null) {
                Toast.makeText(this, "Error in saving note.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            }
        }
        else{
//            values.put(NotesContract.NotesEntry.COLUMN_NOTE_URL, String.valueOf(mImageUri));
            int rowsUpdated = getContentResolver().update(mUri, values1, null,null);
             int rows = getContentResolver().update(mUri, values2, null,null);

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
            case R.id.action_take_image:
                if(hasCamera()){
                    saveImage();
                }
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] Projection = {NotesContract.NotesEntry.COLUMN_ID, NotesContract.NotesEntry.COLUMN_NOTE_TITLE,
                NotesContract.NotesEntry.COLUMN_NOTE, NotesContract.NotesEntry.COLUMN_NOTE_URL};
        return new CursorLoader(getApplicationContext(), mUri, Projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            String noteTitle = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE));
//            mEnterNoteTitle = (EditText) findViewById(R.id.enter_note_title);
//            mEnterNote = (EditText) findViewById(R.id.enter_note);
            imageView = (ImageView)findViewById(R.id.note_image);
            mEnterNoteTitle.setText(noteTitle);
            mEnterNote.setText(note);
            String photoUri = cursor.getString(cursor.getColumnIndex(NotesContract.ImagesEntry.COLUMN_IMAGE_URL));
            if(photoUri!=null) {
                mImageUri = Uri.parse(photoUri);
                Glide.with(this).load(photoUri).into(imageView);
            }

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
    private boolean hasCamera(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        else {
            return false;
        }
    }
    private void saveImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
             //Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                Log.i(TAG, "Image saved at "+ photoURI);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "it's getting called");
        if(requestCode == IMAGE_CAPTURE && data!=null){
            if (resultCode == RESULT_OK){
//                Toast.makeText(this, "Image saved at "+ mImageUri, Toast.LENGTH_LONG).show();
//                Picasso.with(getApplicationContext()).load(mCurrentPhotoPath).into(imageView);
                imageView = (ImageView)findViewById(R.id.note_image);
                Glide.with(this).load(mCurrentPhotoPath).into(imageView);


            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error in saving image", Toast.LENGTH_LONG).show();

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("photopath", mCurrentPhotoPath);


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("photopath")) {
                mCurrentPhotoPath = savedInstanceState.getString("photopath");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        if(!mNoteHasChanged){
            super.onBackPressed();
            return;
        }
// Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private boolean isValid(){
        if(TextUtils.isEmpty(noteTitleString) || TextUtils.isEmpty(noteString)){
            return false;
        }
        else
            return true;
    }
}
