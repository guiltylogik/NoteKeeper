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
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.guiltylogik.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.guiltylogik.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.guiltylogik.notekeeper.ORIGINAL_NOTE_TEXT";
    private static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean mIsNewNote;
    private Spinner mCoursesSpinner;
    private EditText mNoteTitle;
    private EditText mNoteText;
    private int mNewNotePosition;
    private boolean mIsCancelling;
    private ArrayAdapter<CourseInfo> mCoursesAdapter;
    private String mOriginalNoteId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mCoursesSpinner = findViewById(R.id.courses_spinner);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCoursesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        mCoursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCoursesSpinner.setAdapter(mCoursesAdapter);

        readDisplayStateValue();

        if (savedInstanceState == null){
            saveOrinalNote();
        }else {
            resetOriginalNote(savedInstanceState);
        }

        mNoteTitle = findViewById(R.id.title_editText);
        mNoteText = findViewById(R.id.note_text_editText);
        
        if(!mIsNewNote)
            displayNote(mCoursesSpinner, mNoteTitle, mNoteText);
    }

    private void resetOriginalNote(Bundle savedInstanceState) {
        mOriginalNoteId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOrinalNote() {
        if (mIsNewNote)
            return;
        mOriginalNoteId = note.getCourse().getCourseId();
        mOriginalNoteTitle = note.getTitle();
        mOriginalNoteText = note.getText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
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
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNewNotePosition);
            }else {
                restoreOriginalNote();
            }
        } else{
            saveNote();
        }
    }

    private void restoreOriginalNote() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteId);
        note.setCourse(course);
        note.setTitle(mOriginalNoteTitle);
        note.setText(mOriginalNoteText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCoursesAdapter.notifyDataSetChanged();
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
