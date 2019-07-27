package com.guiltylogik.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.guiltylogik.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Spinner coursesSpinner = (Spinner) findViewById(R.id.courses_spinner);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> coursesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesSpinner.setAdapter(coursesAdapter);

        readDisplayStateValue();

        EditText noteTitle = findViewById(R.id.title_editText);
        EditText noteText = findViewById(R.id.note_text_editText);
        
        if(!mIsNewNote)
            displayNote(coursesSpinner, noteTitle, noteText);
    }

    private void displayNote(Spinner coursesSpinner, EditText noteTitle, EditText noteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(note.getCourse());
        coursesSpinner.setSelection(courseIndex);
        noteTitle.setText(note.getTitle());
        noteText.setText(note.getText());
    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if(!mIsNewNote)
            note = DataManager.getInstance().getNotes().get(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
