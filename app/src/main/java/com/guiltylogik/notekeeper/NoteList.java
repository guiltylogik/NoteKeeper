package com.guiltylogik.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NoteList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteList.this, NoteActivity.class));
            }
        });

        InitializeDisplayContent();
    }

    private void InitializeDisplayContent() {

        final ListView notesList =  findViewById(R.id.notes_list);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        ArrayAdapter<NoteInfo> notesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        notesList.setAdapter(notesAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(NoteList.this, NoteActivity.class);
//                NoteInfo note = (NoteInfo) notesList.getItemAtPosition(i);
                intent.putExtra(NoteActivity.NOTE_POSITION, i);
                startActivity(intent);
            }
        });

    }

}
