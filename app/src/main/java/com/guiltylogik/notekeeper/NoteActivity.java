package com.guiltylogik.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.guiltylogik.notekeeper.NOTE_POSITION";
    private static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean mIsNewNote;
    private Spinner mCoursesSpinner;
    private EditText mNoteTitle;
    private EditText mNoteText;
    private int mNewNotePosition;
    private boolean mIsCancelling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mCoursesSpinner = findViewById(R.id.courses_spinner);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> coursesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCoursesSpinner.setAdapter(coursesAdapter);

        readDisplayStateValue();

        mNoteTitle = findViewById(R.id.title_editText);
        mNoteText = findViewById(R.id.note_text_editText);
        
        if(!mIsNewNote)
            displayNote(mCoursesSpinner, mNoteTitle, mNoteText);
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
        if(mIsNewNote){

            DataManager dm = DataManager.getInstance();
            mNewNotePosition = dm.createNewNote();

            note = dm.getNotes().get(mNewNotePosition);

        } else {
            note = DataManager.getInstance().getNotes().get(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            if(mIsNewNote)
                DataManager.getInstance().removeNote(mNewNotePosition);
        } else{
            saveNote();
        }
    }

    private void saveNote() {
        note.setCourse((CourseInfo) mCoursesSpinner.getSelectedItem());
        note.setTitle(mNoteTitle.getText().toString());
        note.setText(mNoteText.getText().toString());
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

        if (id == R.id.action_send_mail) {
            sendMail();
            return true;
        } else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail() {
        CourseInfo course = (CourseInfo) mCoursesSpinner.getSelectedItem();
        String subject = mNoteTitle.getText().toString();
        String text = "See what I learned from Pluralsight course \""+ course.getTitle() + " \"\n" +
                mNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
