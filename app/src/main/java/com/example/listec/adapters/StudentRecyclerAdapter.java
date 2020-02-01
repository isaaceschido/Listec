package com.example.listec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listec.R;
import com.example.listec.models.Student;

import java.util.ArrayList;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.ViewHolder> {

    private ArrayList<Student> mStudents = new ArrayList<>();
    private StudentRecyclerClickListener mStudentRecyclerClickListener;

    public StudentRecyclerAdapter(ArrayList<Student> students, StudentRecyclerClickListener studentRecyclerClickListener) {
        this.mStudents = students;
        mStudentRecyclerClickListener = studentRecyclerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mStudentRecyclerClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRecyclerAdapter.ViewHolder holder, int position) {
        holder.studentTitle.setText(mStudents.get(position).getNombre());
        holder.studentAsistants.setText((String.valueOf(mStudents.get(position).getNasistencias())));
        holder.studentFaltas.setText((String.valueOf(mStudents.get(position).getNfaltas())));
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView studentTitle;
        private TextView studentAsistants;
        private TextView studentFaltas;
        StudentRecyclerClickListener clickListener;

        public ViewHolder(View itemView, StudentRecyclerClickListener clickListener) {
            super(itemView);
            studentTitle = itemView.findViewById(R.id.student_title);
            studentAsistants = itemView.findViewById(R.id.student_asistant);
            studentFaltas = itemView.findViewById(R.id.student_faltas);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onGroupSelected(getAdapterPosition());
        }
    }

    public interface StudentRecyclerClickListener {
        public void onGroupSelected(int position);
    }
}
