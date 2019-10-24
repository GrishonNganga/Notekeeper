package com.example.notekeeper;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() throws Exception{

        final DataManager dm = DataManager.getInstance();
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body text of the note";

        int noteIndex = dm.createNewNote();
        NoteInfo note = dm.getNotes().get(noteIndex);
        note.setCourse(course);
        note.setTitle(noteTitle);
        note.setText(noteText);

        NoteInfo compareNote = dm.getNotes().get(noteIndex);
        assertEquals(compareNote.getCourse(), course);
        assertEquals(compareNote.getTitle(), noteTitle);
        assertEquals(compareNote.getText(), noteText);

    }
}