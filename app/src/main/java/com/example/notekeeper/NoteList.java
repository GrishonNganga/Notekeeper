package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.List;

public class NoteList extends AppCompatActivity {
    private NoteRecyclerAdapter noteRecyclerAdapter;


    //private ArrayAdapter notesAdapter;

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
                Intent intent = new Intent(NoteList.this, MainActivity.class);
                startActivity(intent);
            }
        });

        onCreateDisplay();
    }
    @Override
    protected void onResume() {
        super.onResume();

        //notesAdapter.notifyDataSetChanged();
        noteRecyclerAdapter.notifyDataSetChanged();
    }

    private void onCreateDisplay() {
        /*final ListView noteList = findViewById(R.id.list_note);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        noteList.setAdapter(notesAdapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteList.this, MainActivity.class);
                //NoteInfo note = (NoteInfo) noteList.getItemAtPosition(position);
                intent.putExtra(MainActivity.NOTE_POSITION, position);
                startActivity(intent);
            }

        });
        */
        final RecyclerView notesRecycler = findViewById(R.id.nav_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesRecycler.setLayoutManager(layoutManager);

        final List<NoteInfo> notes = DataManager.getInstance().getNotes();

        noteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        notesRecycler.setAdapter(noteRecyclerAdapter);

    }

}
