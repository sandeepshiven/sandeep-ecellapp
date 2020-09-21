package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class CurrentworkActivityShow extends AppCompatActivity {
    TextView taskTextView;
    TextView headingTextView;
    ImageView imageView;
    ScrollView scrollView;
    String dateShow;
    String dategiven;
    TextView dateTextView;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentwork_show);

        scrollView = findViewById(R.id.scrollView);
        headingTextView = findViewById(R.id.headingTextView);
        imageView = findViewById(R.id.imageView);
        taskTextView = findViewById(R.id.taskTextView);
        dateTextView = findViewById(R.id.dateTextView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Intent intent = getIntent();
        dateShow = intent.getStringExtra("dateShow");

        if(dateShow.contains(","))
        {
            dategiven =  "date";
        }
        else {
            dategiven = "date1";
        }

        FirebaseFirestore.getInstance().collection("Current").whereEqualTo(dategiven,dateShow)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String mess = doc.getString("task");
                        String heading = doc.getString("heading");
                        headingTextView.setText(heading);
                        dateTextView.setText(dateShow);

                        first = databaseReference.child("images").child(dateShow);

                        first.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String link = dataSnapshot.getValue(String.class);
                                Picasso.get().load(link).into(imageView);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        taskTextView.setText(mess);

                    }
                }

                if(task.getResult().isEmpty()){
                    taskTextView.setText("There is no task allotted for this date");
                    dateTextView.setText(dateShow);
                }
            }

        });

    }
}