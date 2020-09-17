package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    Button button;
    Button logout;
    Button deptChatBtn;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    String groupName = "Full Team Chat";
    String userId , deptName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        button = (Button) findViewById(R.id.button);
        logout = (Button) findViewById(R.id.logout);
        deptChatBtn = (Button) findViewById(R.id.DeptChat);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        deptChatBtn.setEnabled(false);

        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            String userEmail = firebaseUser.getEmail();
        }else {
            startActivity(new Intent(DashboardActivity.this,LrAcitivity.class));
        }

        getUserDept();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(DashboardActivity.this, LrAcitivity.class);
                startActivity(logoutIntent);
                Toast.makeText(DashboardActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent teamChatIntent = new Intent(DashboardActivity.this,FullteamchatActivity.class);
                teamChatIntent.putExtra("groupName",groupName);
                startActivity(teamChatIntent);
            }
        });

        deptChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deptChatIntent = new Intent(DashboardActivity.this,DeptchatActivity.class);
                deptChatIntent.putExtra("deptName",deptName);
                startActivity(deptChatIntent);
            }
        });
    }


    private void getUserDept() {

        rootRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    deptName = snapshot.child("department").getValue().toString();
                    deptChatBtn.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}