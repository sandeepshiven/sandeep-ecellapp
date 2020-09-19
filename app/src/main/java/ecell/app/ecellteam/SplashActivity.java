package ecell.app.ecellteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    // Checking the Internet Connection
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

    // Creating Alert dialog for No Internet Connection.
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
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(isOnline())
        {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // If already login than have the code for update UI here
                    // Basically redirecting them directly to Dashboard UI

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null)
                    {
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    else
                    {
                        // If not login then send them to new Activity LR Activity
                        startActivity(new Intent(SplashActivity.this, LrAcitivity.class));
                        finish();
                    }
                }
            },  3000);

        }
        else
        {
            checkNetworkConnection();
        }

    }
}