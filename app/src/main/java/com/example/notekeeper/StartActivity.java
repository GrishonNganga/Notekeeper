package com.example.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NoteRecyclerAdapter noteRecyclerAdapter;
    private RecyclerView notesRecycler;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private CourseRecyclerAdapter courseRecyclerAdapter;
    NoteKeeperOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create openHelper.
        helper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        onCreateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNav();

        noteRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        helper.close();

        super.onDestroy();
    }
    private void updateNav() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView nav_name = view.findViewById(R.id.text_nav_email);
        TextView nav_email = view.findViewById(R.id.text_nav_email);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("user_name", "");
        String email = preferences.getString("email_address", "");

        nav_email.setText(email);
        nav_name.setText(username);
    }

    private void onCreateDisplay() {
        DataManager.loadFromDb(helper);
        notesRecycler = findViewById(R.id.note_items);
        layoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));

        final List<NoteInfo> notes = DataManager.getInstance().getNotes();
        final List<CourseInfo> courses = DataManager.getInstance().getCourses();


        noteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        courseRecyclerAdapter = new CourseRecyclerAdapter(this,courses);
        displayNoteContent();

    }

    private void displayCourseContent() {
       notesRecycler.setLayoutManager(gridLayoutManager);
       notesRecycler.setAdapter(courseRecyclerAdapter);
        selectNavMEnuItem(R.id.nav_courses);
    }

    private void displayNoteContent() {
        notesRecycler.setLayoutManager(layoutManager);
        notesRecycler.setAdapter(noteRecyclerAdapter);

        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        selectNavMEnuItem(R.id.nav_notes);
    }

    private void selectNavMEnuItem(int id) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(StartActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNoteContent();
        } else if (id == R.id.nav_courses) {
            displayCourseContent();
        }  else if (id == R.id.nav_share) {
            handleSelection(R.string.nav_share_message);
        } else if (id == R.id.nav_send) {
            handleSelection((R.string.nav_send_message));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.note_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }
}
