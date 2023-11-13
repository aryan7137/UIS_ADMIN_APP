package com.example.uisadmin.faculty;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewAdapter> {
    private List<TeacherData> list;
    private Context context;


    public TeacherAdapter(List<TeacherData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TeacherViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewAdapter holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TeacherViewAdapter extends RecyclerView.ViewHolder {

        private TextView name, email, post, contact;
        private Button update;
        private ImageView imageView;
        public TeacherViewAdapter(@NonNull View itemView) {
            super(itemView);


        }
    }
}
