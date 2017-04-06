package com.example.admin.noteapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.noteapp.data.NoteDbHelper;
import com.example.admin.noteapp.data.NotesContract;
import com.facebook.stetho.Stetho;

public class AddANote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private NoteDbHelper mDbHelper;
    private NoteCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anote);
        Stetho.initializeWithDefaults(this);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddANote.this, EnterNote.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(1, null, this);
        mAdapter = new NoteCursorAdapter(this,null);
        ListView listView = (ListView)findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Id) {
                Uri mUri = NotesContract.NotesEntry.CONTENT_URI;
                Intent intent = new Intent(AddANote.this, EnterNote.class);
                intent.setData(ContentUris.withAppendedId(mUri,Id));
                startActivity(intent);

            }
        });


    }
 /*private void displayDatabaseInfo(){
     mDbHelper = new NoteDbHelper(this);
     String[] Projection = {NotesContract.NotesEntry.COLUMN_ID, NotesContract.NotesEntry.COLUMN_NOTE_TITLE,
             NotesContract.NotesEntry.COLUMN_NOTE};

     Cursor cursor = db.query(
             NotesContract.NotesEntry.TABLE_NAME,   // The table to query
             Projection,            // The columns to return
             null,                  // The columns for the WHERE clause
             null,                  // The values for the WHERE clause
             null,                  // Don't group the rows
             null,                  // Don't filter by row groups
             null);                   // The sort order

     Cursor cursor = getContentResolver().query(NotesContract.NotesEntry.CONTENT_URI, Projection, null, null, null);
     mAdapter = new NoteCursorAdapter(this,cursor);
     ListView listView = (ListView)findViewById(R.id.list);
     listView.setAdapter(mAdapter);*/


     /*TextView displayView = (TextView) findViewById(R.id.textview_note);

     try {
         // Create a header in the Text View that looks like this:
         //
         // The pets table contains <number of rows in Cursor> pets.
         // _id - name - breed - gender - weight
         //
         // In the while loop below, iterate through the rows of the cursor and display
         // the information from each column in this order.
         displayView.setText("The notes table contains " + cursor.getCount() + " notes.\n\n");
         while(cursor.moveToNext()){
             int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_ID));
             String note = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE));

             displayView.append("\n\n" + rowId + "\t" + note);
         }

     }
 finally {
         cursor.close();
     }*/

    @Override
    protected void onStart() {
        super.onStart();
        //displayDatabaseInfo();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] Projection = {NotesContract.NotesEntry.COLUMN_ID, NotesContract.NotesEntry.COLUMN_NOTE_TITLE,
                NotesContract.NotesEntry.COLUMN_NOTE};
        return new CursorLoader(getApplicationContext(), NotesContract.NotesEntry.CONTENT_URI, Projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       mAdapter.swapCursor(null);
    }
}

