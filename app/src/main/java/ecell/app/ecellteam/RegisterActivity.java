package ecell.app.ecellteam;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //making View objects
    private Button registerButton;
    private EditText userEmail,userPassword,userName;
    private ProgressDialog loadingBar;
    private TextView alreadyHaveAcc;
    private Spinner deptSpinner;

    //making Firebase and database objects
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    //making String Object
    private String department;

    //making. regular expression for password
    private static final Pattern passwordPattern = Pattern.compile("^" +
            "(?=.*[a-z])" +           //at least 1 lower case letter
            "(?=.*[@#$%^&+=])" +      //at least 1 special character
            "(?=.*[0-9])" +           //at least 1 digit
            "(?=\\S+$)" +             //no white spaces
            ".{6,20}" +               //must be at this range
            "$");                     //this must be the end of the password


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting FirebaseAuth and database
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        //calling method to initialize the view objects
        InitializeFields();

        //link if you already have an account
        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling method to create new account
                CreateNewAccount();
            }

        });

    }



    //getting user registered
    private void CreateNewAccount() {

        //taking values for editText
        String userEmailText = userEmail.getText().toString();
        String userPasswordText = userPassword.getText().toString();
        String userNameText = userName.getText().toString();

        //checking for all the necessary condition
        if (TextUtils.isEmpty(userEmailText)){
            userEmail.setError("Please Enter Email...");
        }

        if (TextUtils.isEmpty(userPasswordText)){
            userPassword.setError("Please Enter Email...");
        }

        if (TextUtils.isEmpty(userNameText)){
            userName.setError("Please Enter Your Name");
        }

        if (department.equals("Choose Your Department")){
            TextView errorText = (TextView)deptSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please Choose Your Department");
        }

        //checking for particular pattern and size of email and password
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmailText).matches()){
            userEmail.setError("Please enter a valid email address");
        }

        if (!passwordPattern.matcher(userPasswordText).matches()){
            userPassword.setError("Please enter a valid password");
        }

        if (userPasswordText.length() < 6){
            userPassword.setError("Password must contain at least 6 characters");
        }

        //creating account
        else {

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait,while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(userEmailText,userPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUserId).setValue("");
                        loadingBar.dismiss();
                        StoreInfoInDatabase();
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "ERROR : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }

    }


    //saving user information in database
    private void StoreInfoInDatabase() {

        String userNameText = userName.getText().toString();
        String userEmailText = userEmail.getText().toString();

            String currentUserId = mAuth.getCurrentUser().getUid();
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",userNameText);
            profileMap.put("email",userEmailText);
            profileMap.put("department",department);

            rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        rootRef.child("Users").child(currentUserId).child("LeaderBoardScore").setValue(0);
                        SendUserToDashboardActivity();
                        Toast.makeText(RegisterActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        SendUserToDashboardActivity();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "ERROR : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }


    //initializing objects
    private void InitializeFields() {
        registerButton = (Button) findViewById(R.id.registerButton);
        userEmail = (EditText) findViewById(R.id.emailTxt);
        userPassword = (EditText) findViewById(R.id.passwordTxt);
        userName = (EditText) findViewById(R.id.usernameTxt);
        loadingBar = new ProgressDialog(this);
        alreadyHaveAcc = (TextView) findViewById(R.id.already_have_an_account_link);

        //initializing spinner
        deptSpinner = (Spinner) findViewById(R.id.departmentSpinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.department,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        deptSpinner.setAdapter(arrayAdapter);

        deptSpinner.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        department = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //method to send user to login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    //method to send user to register activity
    private void SendUserToDashboardActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this,DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}