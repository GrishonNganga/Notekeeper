package com.example.notekeeper;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
    private NoteKeeperDatabaseContract(){}

    public static class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COL_COURSE_ID = "course_id";
        public static final String COL_COURSE_TITLE = "course_title";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                +COL_COURSE_ID + "TEXT UNIQUE NOT NULL"
                +", " + COL_COURSE_TITLE + "TEXT NOT NULL " + ")";
    }

    public static class NoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note_info";
        public static final String COL_COURSE_ID = "note_id";
        public static final String COL_NOTE_TITLE = "note_title";
        public static final String COL_NOTE_TEXT = "note_text";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + COL_COURSE_ID + "TEXT NOT NULL"
                +", " + COL_NOTE_TITLE + " TEXT "
                + ", " + COL_NOTE_TEXT + "TEXT NOT NULL " +")";
    }
}
