package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TimeZone;

public class CurrentworkActivity extends AppCompatActivity {
    String dateShow;

    Button button;
    TextView textView;
    TextView taskTextView;
    TextView headingTextView;
    ScrollView scrollView;
    LinearLayout linearLayout;
    String dategiven;
    ImageView imageView1 , imageView2, imageView3;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private  DatabaseReference first;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentwork);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        taskTextView = findViewById(R.id.taskTextView);
        headingTextView = findViewById(R.id.headingTextView);
        imageView1 = findViewById(R.id.imageView1);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();



        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        final long today = MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");
        builder.setSelection(today);
        final MaterialDatePicker materialDatePicker = builder.build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                dateShow = materialDatePicker.getHeaderText();
                textView.setText("Selected Date: \n" +materialDatePicker.getHeaderText());

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

                                first = databaseReference.child("images").child(dateShow);

                                first.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String link = dataSnapshot.getValue(String.class);
                                        Picasso.get().load(link).into(imageView1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                taskTextView.setText(mess);


                            }
                        }else{
                            taskTextView.setText("There is nothing in this date ");
                        }
                    }
                });

            }
        });




    }
}