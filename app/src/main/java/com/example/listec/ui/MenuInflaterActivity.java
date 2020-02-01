package com.example.listec.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.listec.R;
import com.example.listec.adapters.GroupRecyclerAdapter;
import com.example.listec.models.Group;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MenuInflaterActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, GroupRecyclerAdapter.GroupRecyclerClickListener {

    private Button btnList, btnFinGruup, btnDelete;
    private ArrayList<Group> mGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manu_inflater);

        btnList = findViewById(R.id.list_group);

        btnFinGruup = findViewById(R.id.finalize_group);

        btnDelete = findViewById(R.id.delete_group);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onGroupSelected(int position) {
    }
}
