package com.example.notekeeper;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<CourseInfo> course_list;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> course_list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.course_list = course_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_list_courses, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CourseInfo course = course_list.get(i);
        viewHolder.textCourse.setText(course.getTitle());
        viewHolder.course_position = i;
    }

    @Override
    public int getItemCount() {
        return course_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textCourse;
        public int course_position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_courses);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Snackbar.make(v, "Clicked on a Course", Snackbar.LENGTH_SHORT).show();
                }
            });

        }
    }

}