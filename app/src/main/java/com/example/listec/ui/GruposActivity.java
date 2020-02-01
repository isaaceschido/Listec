package com.example.listec.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.listec.adapters.GroupRecyclerAdapter;
import com.example.listec.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.listec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

public class GruposActivity extends AppCompatActivity implements View.OnClickListener, GroupRecyclerAdapter.GroupRecyclerClickListener {


    private static final String TAG = "MainActivity";

    private static final String titulo = "Grupos";

    private Group mGroup;
    private Set<String> mGroupIds = new HashSet<>();
    private FirebaseFirestore mDb;
    private GroupRecyclerAdapter mGroupRecyclerAdapter;
    private ListenerRegistration mGroupEventListener;
    private RecyclerView mGroupRecyclerView;
    private ArrayList<Group> mGroups = new ArrayList<>();
    Button btnList, btnFinGruup, btnDelete;

    //widgets
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnList = findViewById(R.id.list_group);

        btnFinGruup = findViewById(R.id.finalize_group);

        btnDelete = findViewById(R.id.delete_group);

        setContentView(R.layout.activity_grupos);
        mProgressBar = findViewById(R.id.progressBar);
        mGroupRecyclerView = findViewById(R.id.groups_recycler_view);
        Toolbar toolbar = findViewById(R.id.toolbar_groups);
        this.setTitle(titulo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.fab_create_group).setOnClickListener(this);


        mDb = FirebaseFirestore.getInstance();

        getIncomingIntent();

        initSupportActionBar();

        initGroupRecyclerView();

        getGroups();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(getString(R.string.intent_groups))) {
            mGroup = getIntent().getParcelableExtra(getString(R.string.intent_groups));
            setGroupName();
        }
    }

    private void initSupportActionBar() {
        setTitle(getString(R.string.collection_groups));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_create_group: {
                newGroupDialog();
            }
        }
    }

    private void initGroupRecyclerView() {
        mGroupRecyclerAdapter = new GroupRecyclerAdapter(mGroups, this);
        mGroupRecyclerView.setAdapter(mGroupRecyclerAdapter);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getGroups() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        final CollectionReference groupsCollection = mDb
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
                                if (!mGroupIds.contains(group.getGroup_id()) && (group.getStatus().equals("n"))) {
                                    mGroupIds.add(group.getGroup_id());
                                    mGroups.add(group);
                                }
                            }
                            Log.d(TAG, "onEvent: number of chatrooms: " + mGroups.size());
                            mGroupRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    private void newGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a new group");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().equals("")) {
                    buildNewGroup(input.getText().toString());
                } else {
                    Toast.makeText(GruposActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void buildNewGroup(String groupName) {

        final Group group = new Group();
        group.setTitle(groupName.toUpperCase());

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newGroupRef = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups))
                .document();

        group.setGroup_id(newGroupRef.getId());
        group.setStatus("n");

        newGroupRef.set(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    navGroupActivity(group);
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGroupEventListener != null) {
            mGroupEventListener.remove();
        }
    }

    private void setTotalAsistants(final Group group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you want finalize?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DocumentReference groupsCollection = mDb
                        .collection(getString(R.string.collection_users))
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection(getString(R.string.collection_groups))
                        .document(group.getGroup_id());
                groupsCollection.update("status", "f")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "Finalized, check statistics.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "No finalized.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("No   -   ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Ok :) be careful.", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    private void finalizedGroup(final Group group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you want finalize?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DocumentReference groupsCollection = mDb
                        .collection(getString(R.string.collection_users))
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection(getString(R.string.collection_groups))
                        .document(group.getGroup_id());



                groupsCollection.update("status", "f")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "Finalized, check statistics.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "No finalized.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("No   -   ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Ok :) be careful.", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void deleteGroup(final Group group) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DocumentReference groupsCollection = mDb
                        .collection(getString(R.string.collection_users))
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection(getString(R.string.collection_groups))
                        .document(group.getGroup_id());
                groupsCollection.delete();

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Deleted please reload.", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No - ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Ok :) be careful.", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }


    public void navGroupActivity(final Group group) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What you want to do");

        builder.setIcon(R.drawable.checklist_icon);

        builder.setMessage("Choose an action");

        builder.setPositiveButton("List", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GruposActivity.this, StudentsActivity.class);
                intent.putExtra(getString(R.string.intent_groups), group);
                startActivity(intent);
            }
        });

        builder.setNeutralButton("Finalized group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finalizedGroup(group);
            }
        });
        builder.setNegativeButton("Delete group   -   ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGroup(group);
            }
        });

        builder.show();


    }


    @Override
    public void onGroupSelected(final int position) {

/*
        AlertDialog.Builder builder = new AlertDialog.Builder(GruposActivity.this);
        View view = getLayoutInflater().inflate(R.layout.activity_manu_inflater, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        */

        navGroupActivity(mGroups.get(position));
    }

    private void setGroupName() {
        getSupportActionBar().setTitle(mGroup.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


}
