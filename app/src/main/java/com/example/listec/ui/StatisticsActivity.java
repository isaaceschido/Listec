package com.example.listec.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.listec.R;
import com.example.listec.adapters.StatisticsRecyclerAdapter;
import com.example.listec.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener, StatisticsRecyclerAdapter.StatisticsRecyclerClickListener {

    private static final String TAG = "MainActivity";

    private Group mGroup;
    private Set<String> mGroupIds = new HashSet<>();
    private FirebaseFirestore mDb;
    private StatisticsRecyclerAdapter statisticsRecyclerAdapter;
    private ListenerRegistration mGroupEventListener;
    private RecyclerView mGroupRecyclerView;
    private ArrayList<Group> mGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.setTitle("Statistics");
        mGroupRecyclerView = findViewById(R.id.statistics_recycler_view);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = FirebaseFirestore.getInstance();

        getIncomingIntent();

        initGroupRecyclerView();

        getGroups();
    }


    private void getGroups() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        CollectionReference groupsCollection = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups));

        mGroupEventListener = groupsCollection
                .orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Log.d(TAG, "onEvent: called.");

                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Group group = doc.toObject(Group.class);

                                if (!mGroupIds.contains(group.getGroup_id()) && (group.getStatus().equals("f"))) {
                                    mGroupIds.add(group.getGroup_id());
                                    mGroups.add(group);
                                }
                            }
                            Log.d(TAG, "onEvent: number of chatrooms: " + mGroups.size());
                            statisticsRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initGroupRecyclerView() {
        statisticsRecyclerAdapter = new StatisticsRecyclerAdapter(mGroups, this);
        mGroupRecyclerView.setAdapter(statisticsRecyclerAdapter);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(getString(R.string.intent_groups))) {
            mGroup = getIntent().getParcelableExtra(getString(R.string.intent_groups));
        }
    }

    private void navGroupActivity(Group group) {
        Intent intent = new Intent(StatisticsActivity.this, StudentsStatisticsActivity.class);
        intent.putExtra(getString(R.string.intent_groups), group);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onGroupSelected(int position) {
        navGroupActivity(mGroups.get(position));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStatisticsSelected(int position) {
        navGroupActivity(mGroups.get(position));
    }
}
