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

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    private ArrayList<Group> mGroups = new ArrayList<>();
    private GroupRecyclerClickListener mGroupRecyclerClickListener;

    public GroupRecyclerAdapter(ArrayList<Group> groups, GroupRecyclerClickListener groupRecyclerClickListener) {
        this.mGroups = groups;
        mGroupRecyclerClickListener = groupRecyclerClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mGroupRecyclerClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerAdapter.ViewHolder holder, int position) {
        ((ViewHolder) holder).groupTitle.setText(mGroups.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView groupTitle;
        GroupRecyclerClickListener clickListener;

        public ViewHolder(View itemView, GroupRecyclerClickListener clickListener) {
            super(itemView);
            groupTitle = itemView.findViewById(R.id.group_title);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onGroupSelected(getAdapterPosition());
        }
    }

    public interface GroupRecyclerClickListener {
        public void onGroupSelected(int position);
    }
}