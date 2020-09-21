package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AnnouncementActivity extends AppCompatActivity {
    private ListView listItems;

    TextView tv;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        listItems = (ListView) findViewById(R.id.listView1);
        button = findViewById(R.id.button);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);
                /*  tv.setTextSize(20); */
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);

                // Change the item text size
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                tv.setPadding(30,0,30,40);

                // Generate ListView Item using TextView
                return view;
            }
        };

        listItems.setAdapter(arrayAdapter);

        // Get Reference of object
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore.getInstance().collection("Information")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        arrayList.add(doc.getString("announcement"));

                        Log.d("Document", doc.getId() + "=>" + doc.getData());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });


    }
}