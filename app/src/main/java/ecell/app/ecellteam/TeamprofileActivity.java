package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TeamprofileActivity extends AppCompatActivity {
    private RecyclerView recycler_view;
    private ArrayList<Member> members;
    Member_Adapter1 adapter;private String department1; private Spinner deptSpinner;

    //initializing spinner
    private void InitializeFields() {
        //initializing spinner
        deptSpinner = (Spinner) findViewById(R.id.departmentSpinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.department, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        deptSpinner.setAdapter(arrayAdapter);
        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                department1 =parent.getItemAtPosition(position).toString();
                if (department1.equals("Please Choose Department")){
                    TextView errorText = (TextView)deptSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Please Choose Department");
                }
                loadMembers();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setupRecyclerView(){

        adapter = new Member_Adapter1(members);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
    }

    //Loading tEAM MEMBER NAME DEPARTMENT VISE
    private void loadMembers() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        ref.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                members.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    String department = ds.child("department").getValue(String.class);
                    if (department.equals(department1)) {
                        Log.i("reached", department);
                        Member member = new Member();
                        member.setName(name);
                        member.setDepartment(department);
                        members.add(member);
                    }
                }
                setupRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamprofile);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        members = new ArrayList<>();

        InitializeFields();
        //setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#f58220"));
        //actionBar.setBackgroundDrawable(colorDrawable);
    }}