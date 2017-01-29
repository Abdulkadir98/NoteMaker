package com.example.admin.noteapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.admin.noteapp.data.NotesContract;

/**
 * Created by admin on 1/28/2017.
 */

public class NoteCursorAdapter extends CursorAdapter {
    public NoteCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView noteTitleView = (TextView)view.findViewById(R.id.note_title_string);
        TextView noteView = (TextView)view.findViewById(R.id.note_string);

        String noteTitle = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE_TITLE));
        String note = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_NOTE));

        noteTitleView.setText(noteTitle);
        noteView.setText(note);

    }
}
