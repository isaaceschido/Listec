package com.example.listec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listec.R;
import com.example.listec.models.Group;
import com.example.listec.models.Student;

import java.util.ArrayList;

public class StudentStatisticsRA extends RecyclerView.Adapter<StudentStatisticsRA.ViewHolder> {

    private ArrayList<Student> mStudents = new ArrayList<>();
    private ArrayList<Group> mGroups = new ArrayList<>();
    private StudentStaticsClickListener mStudentRecyclerClickListener;

    public StudentStatisticsRA(ArrayList<Student> students, StudentStaticsClickListener studentRecyclerClickListener) {
        this.mStudents = students;
        mStudentRecyclerClickListener = studentRecyclerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_students_staticslist_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mStudentRecyclerClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentStatisticsRA.ViewHolder holder, int position) {

        float asistencias = mStudents.get(position).getNasistencias();

        float ast = mStudents.get(position).getPromedioasistencias();

        //float grupos = mGroups.get(position).getGroup_id().length();

        float promedio = (asistencias / ast) * 100;

        holder.studentTitle.setText(mStudents.get(position).getNombre());
        holder.studentAsistants.setText((String.valueOf(mStudents.get(position).getNasistencias())));
        holder.studentFaltas.setText((String.valueOf(mStudents.get(position).getNfaltas())));
        //holder.studentPromedio.setText((String.valueOf(mStudents.get(position).getPromedioasistencias())));
        holder.studentPromedio.setText((String.valueOf(promedio)));
        //holder.studentPromedio.setText((String.valueOf((mStudents.get(position).getNasistencias() / mStudents.get(position).getPromedioasistencias()) )));

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
        private TextView studentPromedio;
        StudentStaticsClickListener clickListener;

        public ViewHolder(View itemView, StudentStaticsClickListener clickListener) {
            super(itemView);
            studentTitle = itemView.findViewById(R.id.statistics_student_title);
            studentAsistants = itemView.findViewById(R.id.statistics_student_asistant);
            studentFaltas = itemView.findViewById(R.id.statistics_student_faltas);
            studentPromedio = itemView.findViewById(R.id.statistics_student_avarage);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onGroupSelected(getAdapterPosition());
        }
    }

    public interface StudentStaticsClickListener {
        public void onGroupSelected(int position);
    }
}