package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import android.view.WindowManager;

public class DeptchatActivity extends AppCompatActivity {


    //making View objects
    private Toolbar mToolBar;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;

    //making layout objects
    private LinearLayout layout;

    //making Firebase and database objects
    private FirebaseAuth mAuth;
    private DatabaseReference userReference , groupNameReference , groupMessageKeyRef;

    //making String objects
    private String groupChatName , currentUserId , currentUserName , currentTime , currentDate , username;


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {

            Log.d("Network", "Not Connected");
            return false;
        }
    }

    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Kindly, Turn On Internet Connection To Continue");
        builder.setNegativeButton("EXIT APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deptchat);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //getting values through intent from dashboard activity
        groupChatName = getIntent().getExtras().get("deptName").toString();
        username = getIntent().getExtras().get("userName").toString();

        //setting FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //getting current user
        currentUserId = mAuth.getCurrentUser().getUid();

        //giving required references to the database objects
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupChatName);

        //calling method to initialize the view objects
        InitializeFielda();

        //calling method to get user information
        getUserInfo();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling method to save messages to database
                saveMessageInToDatabase();

                //clearing editText area for next message
                messageArea.setText("");

                //making scroll view to scroll down to the most recent message
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //checking if messages existed
                if (snapshot.exists()) {
                    //calling method to show all the messages
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


    //method to generate textView
    public void addMessageBox(String messageFormat , int key){
        //setting textView and its properties
        TextView textView = new TextView(DeptchatActivity.this);
        textView.setTextSize(18);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText(messageFormat);

        //setting layout with textView
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(15, 15, 15, 15);
        textView.setLayoutParams(lp);

        //checking user
        if(key == 1) {
            lp.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        //adding layout to textView
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }



    private void DisplayMessages(DataSnapshot snapshot) {

        //using iterator to get the messages
        Iterator iterator = snapshot.getChildren().iterator();

        //using loop to print all the messages
        while (iterator.hasNext()){

            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();


            //setting messages format
            String myMsgFormat = "You :-  " + chatTime + "\n\n" + chatMessage;
            String otherMsgFormat = chatName + "  "+ chatTime + ":-\n\n" + chatMessage;

            //checking if the message is been sent by the user itself and calling method to generate text view
            if(chatName.equals(username)){
                addMessageBox(myMsgFormat , 1);
            }
            else{
                addMessageBox(otherMsgFormat ,2);
            }

            

        }
        
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });


    }

    //method to save data to database
    private void saveMessageInToDatabase() {

        //getting message from editText
        String message = messageArea.getText().toString();

        //setting specific key for every message
        String messageKey = groupNameReference.push().getKey();

        //checking weather text is empty or not
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show();
        }else {

            //getting current date
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            //getting current time
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            //storing data in database
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

    //method to get name of user who send the message
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


    //initializing objects
    private void InitializeFielda() {
        mToolBar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolBar);
        //setting name of group chat
        getSupportActionBar().setTitle(groupChatName);


        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);


    }

}
