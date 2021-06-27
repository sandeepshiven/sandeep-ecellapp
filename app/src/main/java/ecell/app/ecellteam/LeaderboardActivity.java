package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recycler_view;
    private ArrayList<Member> members;
    MemberAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        members = new ArrayList<>();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#f58220"));
        actionBar.setBackgroundDrawable(colorDrawable);
        loadMembers();


    }
    private void setupRecyclerView(){

        adapter = new MemberAdapter(members);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
    }


    private void loadMembers(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        ref.addValueEventListener(new com.google.firebase.database.ValueEventListener(){

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                members.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    Log.i("name", name);
                    String department = ds.child("department").getValue(String.class);
                    Log.i("department", department);
                    Integer score = ds.child("LeaderBoardScore").getValue(Integer.class);
                    if(score == null){
                        ds.child("LeaderBoardScore").getRef().setValue(0);
                        score = 0;
                    }
                    Log.i("score", score.toString());
                    Member member = new Member();
                    member.setName(name);
                    member.setDepartment(department);
                    member.setScore(score);
                    members.add(member);
                }
                Collections.sort(members, Collections.reverseOrder());

                setupRecyclerView();
                showChampions();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
    public void showChampions(){
    }


}