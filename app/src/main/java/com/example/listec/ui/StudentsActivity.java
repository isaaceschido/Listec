package com.example.listec.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.listec.R;
import com.example.listec.adapters.StudentRecyclerAdapter;
import com.example.listec.models.Group;
import com.example.listec.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class StudentsActivity extends AppCompatActivity implements View.OnClickListener, StudentRecyclerAdapter.StudentRecyclerClickListener {


    private static final String TAG = "Student list";

    private Group mGroup;
    private Set<String> mStudentsIds = new HashSet<>();
    private FirebaseFirestore mDb;
    private StudentRecyclerAdapter mStudentRecyclerAdapter;
    private RecyclerView mStudentRecyclerView;
    private ListenerRegistration mStudentEventListener;
    private ArrayList<Student> mStudents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mStudentRecyclerView = findViewById(R.id.student_recycler_view);
        this.setTitle(TAG);
        setSupportActionBar(toolbar);
        findViewById(R.id.fab_add_students).setOnClickListener(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = FirebaseFirestore.getInstance();

        getIncomingIntent();

        initStudentRecyclerView();

        getStudents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_students: {
                newStudentDialog();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getStudents() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        CollectionReference studentCollection = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups))
                .document(mGroup.getGroup_id())
                .collection(getString(R.string.collection_students));

        mStudentEventListener = studentCollection
                .orderBy("nombre", Query.Direction.ASCENDING)
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

                                Student student = doc.toObject(Student.class);
                                if (!mStudentsIds.contains(student.getStundent_id())) {
                                    mStudentsIds.add(student.getStundent_id());
                                    mStudents.add(student);
                                }
                            }
                            Log.d(TAG, "onEvent: number of chatrooms: " + mStudents.size());
                            mStudentRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(getString(R.string.intent_groups))) {
            mGroup = getIntent().getParcelableExtra(getString(R.string.intent_groups));
        }
    }

    private void newStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a new student");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().equals("")) {
                    buildNewStudent(input.getText().toString());
                } else {
                    Toast.makeText(StudentsActivity.this, "Enter a new student", Toast.LENGTH_SHORT).show();
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

    private void buildNewStudent(String studentName) {

        final Student student = new Student();
        student.setGrupo(mGroup);
        student.setNasistencias(0);
        student.setNombre(studentName.toUpperCase());

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newStudentpRef = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups))
                .document(mGroup.getGroup_id())
                .collection(getString(R.string.collection_students))
                .document();

        student.setStundent_id(newStudentpRef.getId());

        newStudentpRef.set(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    navStudentAction(student);
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void IsHeShePresent(final Student student) {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newStudentpRef = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups))
                .document(mGroup.getGroup_id())
                .collection(getString(R.string.collection_students))
                .document(student.getStundent_id());
        newStudentpRef.update("nasistencias", (student.getNasistencias() + 1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Asistencia.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No marcado.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void IsHeSheNotPresent(final Student student) {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newStudentpRef = mDb
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_groups))
                .document(mGroup.getGroup_id())
                .collection(getString(R.string.collection_students))
                .document(student.getStundent_id());
        newStudentpRef.update("nfaltas", (student.getNfaltas() + 1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Inasistencia marcada.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No marcado.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navStudentAction(final Student student) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Is he/she present?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Metodo para marcar la asistencia
                IsHeShePresent(student);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IsHeSheNotPresent(student);
            }
        });

        builder.show();
    }

    private void initStudentRecyclerView() {
        mStudentRecyclerAdapter = new StudentRecyclerAdapter(mStudents, this);
        mStudentRecyclerView.setAdapter(mStudentRecyclerAdapter);
        mStudentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onGroupSelected(int position) {
        navStudentAction(mStudents.get(position));
    }
}
