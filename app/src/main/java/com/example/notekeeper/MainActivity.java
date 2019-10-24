package com.example.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String NOTE_Id = "com.example.notekeeper.NOTE_Id";
    public static final int ID_NOT_SET = -1;
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
    private int _Id;
    private NoteKeeperOpenHelper openHelper;
    private SQLiteDatabase db;
    private Cursor notesCursor;
    private int courseIdPos;
    private int noteTitlePos;
    private int noteTextPos;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        openHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openHelper = new NoteKeeperOpenHelper(this);
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
            //saveOriginalData();
        } else {
            restoreOriginalStateValues(savedInstanceState);
        }

        title = findViewById(R.id.text_note_title);
        note = findViewById(R.id.text_note_text);

        if(!mIsNewNote)

            loadNoteData();
        else Toast.makeText(this, "Unsuccessful",Toast.LENGTH_SHORT).show();
    }

    private void loadNoteData() {
        db = openHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " = ? ";

        String[] selectionArgs = {String.valueOf(_Id)};

        String[] cols = {
                NoteInfoEntry.COL_COURSE_ID,
                NoteInfoEntry.COL_NOTE_TITLE,
                NoteInfoEntry.COL_NOTE_TEXT
        };
        notesCursor = db.query(NoteInfoEntry.TABLE_NAME, cols, selection, selectionArgs, null, null, null);
        notesCursor.moveToNext();
        courseIdPos = notesCursor.getColumnIndex(NoteInfoEntry.COL_COURSE_ID);
        noteTitlePos = notesCursor.getColumnIndex(NoteInfoEntry.COL_NOTE_TITLE);
        noteTextPos = notesCursor.getColumnIndex(NoteInfoEntry.COL_NOTE_TEXT);

        displayNote();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastItem = DataManager.getInstance().getNotes().size() - 1;

        //Refactored following lines because the Notes have been sorted on querying thus not factual

        /*if (_Id == lastItem)
            Toast.makeText(this, "This is the last note",Toast.LENGTH_SHORT).show();
        menuItem.setEnabled(_Id < lastItem);*/
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

    private void displayNote() {
        //Populate Data from the database by the cursor. Check out loadNoteData().
        //Get the strings contained on each columns.
        String courseId = notesCursor.getString(courseIdPos);
        String notesTitle = notesCursor.getString(noteTitlePos);
        String notesText = notesCursor.getString(noteTextPos);

        //The Spinner is a little bit different.
        //First get list of courses. Get the course itself from the list of courses(Because the previous courseId was a String)
        //Then finally get the index of the course which will be used to populate the Spinner.
        List<CourseInfo> courseInfos = DataManager.getInstance().getCourses();
        CourseInfo course = DataManager.getInstance().getCourse(courseId);
        int courseIndex = courseInfos.indexOf(course);
        spinnerCourses.setSelection(courseIndex);

        //Actual populating to the rest of the UI
        title.setText(notesTitle);
        note.setText(notesText);
        Log.v("Title display", "The title is "+ title.getText().toString());
        Log.v("Text display", "The note is "+ note.getText().toString());
        Log.v("Spinner display", "The spinner is selected to "+ spinnerCourses.getSelectedItem().toString());
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
    }

    //Pull Data passed from the parcelable
    private void readDisplayData() {
        Intent intent = getIntent();
        _Id = intent.getIntExtra(NOTE_Id, ID_NOT_SET);

        mIsNewNote = _Id == ID_NOT_SET;

        if (mIsNewNote) {
            createNewNote();
        }
        else{
            //After commenting preceeding line errors arised as will follow

            //mNote = DataManager.getInstance().getNotes().get(_Id);

            //1. mNote triggered null pointer exception.
            //2. On onCreate() saveOriginal() failed as mNote was null thus bringing up an error.
            //In short watch out for mNote from being null...

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
        _Id++;
        mNote = DataManager.getInstance().getNotes().get(_Id);
        saveOriginalData();
        displayNote();
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
            //Saves the selcted note on Pressing back or any other onPause trigger.
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
        //Made the mNote null because apparently it had a null point exception ...
        mNote = new NoteInfo(null, null, null, null);
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
