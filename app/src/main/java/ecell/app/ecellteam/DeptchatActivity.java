package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class DeptchatActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextView groupNameText;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference userReference , groupNameReference , groupMessageKeyRef;

    private String groupChatName , currentUserId , currentUserName , currentTime , currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deptchat);

        Intent intent = getIntent();
        groupChatName = intent.getStringExtra("deptName");

        // Log.i("dept","Hello " + deptName);
        Toast.makeText(DeptchatActivity.this, "Group " + groupChatName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupChatName);

        InitializeFielda();


        getUserInfo();


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageInToDatabase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){

                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot snapshot) {

        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext()){

            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName + " : \n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }

    }

    private void saveMessageInToDatabase() {

        String message = userMessageInput.getText().toString();
        String messageKey = groupNameReference.push().getKey();


        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show();
        }else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());


            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());


            HashMap<String , Object> groupMessageKey = new HashMap<>();
            groupNameReference.updateChildren(groupMessageKey);
            groupMessageKeyRef = groupNameReference.child(messageKey);

            HashMap<String , Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            groupMessageKeyRef.updateChildren(messageInfoMap);


        }
    }

    private void getUserInfo() {

        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUserName = snapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void InitializeFielda() {
        mToolBar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(groupChatName);


        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessage = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);

        displayTextMessage.setText("");


    }

}