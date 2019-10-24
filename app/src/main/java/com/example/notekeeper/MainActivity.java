package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    private NoteInfo mNote;

    private boolean mIsNewNote;
    private Spinner spinnerCourses;
    private EditText title;
    private EditText note;
    private int newNote;
    private boolean isCancelling;
    private String originalCourseId;
    private String originalText;
    private String originalTitle;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Spinner
        spinnerCourses = findViewById(R.id.spinner_courses);

        //Populate Spinner
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        //Populate stuff
        readDisplayData();
        if (savedInstanceState == null){
            saveOriginalData();
        } else {
            restoreOriginalStateValues(savedInstanceState);
        }

        title = findViewById(R.id.text_note_title);
        note = findViewById(R.id.text_note_text);

        if(!mIsNewNote)

            displayNote(spinnerCourses, title, note);
        else Toast.makeText(this, "Unsuccessful",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastItem = DataManager.getInstance().getNotes().size() - 1;
        if (position == lastItem)
            Toast.makeText(this, "This is the last note",Toast.LENGTH_SHORT).show();
        menuItem.setEnabled(position < lastItem);
        return super.onPrepareOptionsMenu(menu);
    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
            originalCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
            originalTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
            originalText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, originalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, originalTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, originalText);
    }

    private void saveOriginalData() {
        if (mIsNewNote)
            return;
        originalCourseId = mNote.getCourse().getCourseId();
        originalTitle = mNote.getTitle();
        originalText = mNote.getText();

    }

    private void displayNote(Spinner spinnerCourses, EditText title, EditText note) {
        List<CourseInfo> courseInfos = DataManager.getInstance().getCourses();
        int courseIndex = courseInfos.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        title.setText(mNote.getTitle());
        String showTitle = title.getText().toString();
        Log.v("Title display", "The title is "+showTitle);
        Toast.makeText(this, "Successful",Toast.LENGTH_SHORT).show();
        note.setText(mNote.getText());

    }

    //Pull Data passed from the parcelable
    private void readDisplayData() {
        Intent intent = getIntent();
        position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        mIsNewNote = position == POSITION_NOT_SET;

        if (mIsNewNote) {
            createNewNote();
        }
        else{
            mNote = DataManager.getInstance().getNotes().get(position);
        }

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        newNote = dm.createNewNote();
        mNote = dm.getNotes().get(newNote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();

        }else if (id == R.id.action_canel_note){
            isCancelling = true;
            finish();
        } else if (id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        saveNote();
        position++;
        mNote = DataManager.getInstance().getNotes().get(position);
        saveOriginalData();
        displayNote(spinnerCourses, title, note);
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isCancelling){
            if (mIsNewNote)
            DataManager.getInstance().removeNote(newNote);
            else {
                storePreviousStateValues();
            }
        }
        else {
            saveNote();
        }
    }

    private void storePreviousStateValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalCourseId);
        mNote.setCourse(course);
        mNote.setTitle(originalTitle);
        mNote.setText(originalText);
    }


    private void saveNote() {
        mNote.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        mNote.setTitle(title.getText().toString());
        mNote.setText(note.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = title.getText().toString();
        String text = "Check out what I learnt at the Pluralsight course \""
                +course.getTitle() +"\"\n" + note.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent)
        ;
    }
}
