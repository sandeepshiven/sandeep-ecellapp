package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    //making objects of the following Views
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private Button newAccBtn;
    private TextView messageText;

    //object of FirebaseAuth for authentication
    private FirebaseAuth mAuth;

    //object of Progress Dialoug Bar
    private ProgressDialog loadingBar;

    //Regular Expression for password
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
        setContentView(R.layout.activity_login);

        //initializing objects of different Views with their respected id...
        emailText = (EditText) findViewById(R.id.login_email);
        passwordText = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginButton);
        messageText = (TextView) findViewById(R.id.messageText);
        newAccBtn = (Button) findViewById(R.id.createAccButton);

        //initializing FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //initializing ProgressDialoug for this activity
        loadingBar = new ProgressDialog(this);

        //setting onClickListner for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageText.setVisibility(View.INVISIBLE);

                //Calling method for login authentication
                AllowUserToLogin();
            }
        });

        newAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void AllowUserToLogin() {

        //Storing email and password in string
        String userEmailText = emailText.getText().toString();
        String userPasswordText = passwordText.getText().toString();


        //checking weather email or password is empty or not
        if (userEmailText.isEmpty()){
            emailText.setError("This field is required");
        }

        if (userPasswordText.isEmpty()){
            passwordText.setError("This field is required");
        }

        if (userEmailText.isEmpty() || userPasswordText.isEmpty()){

            if (userEmailText.isEmpty()){
                emailText.setError("This field is required");
            }

            if (userPasswordText.isEmpty()){
                passwordText.setError("This field is required");
            }
        }

        //checking for particular pattern and size of email and password

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmailText).matches()){
            emailText.setError("Please enter a valid email address");
        }

        if (!passwordPattern.matcher(userPasswordText).matches()){
            passwordText.setError("Please enter a valid password");
        }

        if (userPasswordText.length() < 6){
            passwordText.setError("Password must contain at least 6 characters");
        }

        //if .everything is correct then matching email and password with database using FirebaseAuth

        else {

            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(userEmailText,userPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //if login is successful

                    if (task.isSuccessful()){

                        //calling method to send user to Dashboard

                        SendUserToDashboard();
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                    }

                    //if login is unsuccessful

                    else {
                        messageText.setVisibility(View.VISIBLE);

                        //displaying unsuccessful message

                        messageText.setText("your email or password is not correct");
                        loadingBar.dismiss();
                    }
                }
            });
        }
}


    // method to send user to Dashboard

    private void SendUserToDashboard() {
        Intent mainIntent = new Intent(LoginActivity.this,DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    }