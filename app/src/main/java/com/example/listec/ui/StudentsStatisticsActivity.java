package com.example.listec.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.listec.R;
import com.example.listec.adapters.StudentStatisticsRA;
import com.example.listec.models.Group;
import com.example.listec.models.Student;
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

public class StudentsStatisticsActivity extends AppCompatActivity implements View.OnClickListener, StudentStatisticsRA.StudentStaticsClickListener {

    private static final String TAG = "MainActivity";

    private Student mStudent;
    private Group mGroup;
    private Set<String> mStudentsIds = new HashSet<>();
    private FirebaseFirestore mDb;
    private StudentStatisticsRA mStatisticsRecyclerAdapter;
    private ListenerRegistration mStudentEventListener;
    private RecyclerView mStudentRecyclerView;
    private ArrayList<Student> mStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_statistics);
        this.setTitle("Statics students");
        mStudentRecyclerView = findViewById(R.id.students_statistics_recycler_view);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = FirebaseFirestore.getInstance();

        getIncomingIntent();

        initGroupRecyclerView();

        getStudents();
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
        }
    }

    private void initGroupRecyclerView() {
        mStatisticsRecyclerAdapter = new StudentStatisticsRA(mStudents, this);
        mStudentRecyclerView.setAdapter(mStatisticsRecyclerAdapter);
        mStudentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                            mStatisticsRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onGroupSelected(int position) {

    }
}
