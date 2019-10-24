package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<NoteInfo> note_list;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> note_list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.note_list = note_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_list_note, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        NoteInfo note = note_list.get(i);
        viewHolder.textCourses.setText(note.getCourse().getTitle());
        viewHolder.textTitle.setText(note.getTitle());
        viewHolder.note_position = i;

    }

    @Override
    public int getItemCount() {
        return note_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textTitle;
        public final TextView textCourses;
        public int note_position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourses = itemView.findViewById(R.id.text_courses);
            textTitle = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(MainActivity.NOTE_POSITION, note_position);
                    context.startActivity(intent);
                }
            });

        }
    }

}