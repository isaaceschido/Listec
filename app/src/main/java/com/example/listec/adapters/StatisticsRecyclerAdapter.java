package com.example.listec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listec.R;
import com.example.listec.models.Group;

import java.util.ArrayList;

public class StatisticsRecyclerAdapter extends RecyclerView.Adapter<StatisticsRecyclerAdapter.ViewHolder> {

    private ArrayList<Group> mGroups = new ArrayList<>();
    private StatisticsRecyclerClickListener mStatisticsRecyclerClickListener;

    public StatisticsRecyclerAdapter(ArrayList<Group> groups, StatisticsRecyclerClickListener statisticsRecyclerClickListener) {
        this.mGroups = groups;
        mStatisticsRecyclerClickListener = statisticsRecyclerClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_statistics_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mStatisticsRecyclerClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsRecyclerAdapter.ViewHolder holder, int position) {
        ((ViewHolder) holder).groupTitle.setText(mGroups.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView groupTitle;
        StatisticsRecyclerClickListener clickListener;

        public ViewHolder(View itemView, StatisticsRecyclerClickListener clickListener) {
            super(itemView);
            groupTitle = itemView.findViewById(R.id.statistics_title);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onStatisticsSelected(getAdapterPosition());
        }
    }

    public interface StatisticsRecyclerClickListener {
        public void onStatisticsSelected(int position);
    }
}