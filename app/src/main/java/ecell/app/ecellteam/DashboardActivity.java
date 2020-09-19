package ecell.app.ecellteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView drawer;
    GridLayout gridLayout;
    CardView cardView;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    String groupName = "Full Team Chat";
    String userId, deptName, userName, email;
    CardView deptchat, teamchat;
    TextView name;
    View hview;

    /*--------------Activity click Handling -----------------*/
    public void activity(View view) {
        switch (view.getId()) {
            case R.id.deparment:
                Intent deptChatIntent = new Intent(DashboardActivity.this, DeptchatActivity.class);
                deptChatIntent.putExtra("deptName", deptName);
                deptChatIntent.putExtra("userName", userName);
                startActivity(deptChatIntent);
                Toast.makeText(this, "Welocome to Deparment chat", Toast.LENGTH_SHORT).show();
                break;
            case R.id.teamchat1:
                Toast.makeText(this, "Welocome to Teamchat", Toast.LENGTH_SHORT).show();
                Intent fullteamchatIntent = new Intent(DashboardActivity.this, FullteamchatActivity.class);
                fullteamchatIntent.putExtra("groupName", groupName);
                fullteamchatIntent.putExtra("userName", userName);
                startActivity(fullteamchatIntent);
                break;
            case R.id.announcement1:
                startActivity(new Intent(DashboardActivity.this, AnnouncementActivity.class));
                break;

            case R.id.leaderboard:
                startActivity(new Intent(DashboardActivity.this, LeaderboardActivity.class));
                Toast.makeText(this, "Welcome to LEADERBOARD", Toast.LENGTH_SHORT).show();
                break;
            case R.id.work:
                startActivity(new Intent(DashboardActivity.this, CurrentworkActivity.class));
                Toast.makeText(this, "Welocome to Current work", Toast.LENGTH_SHORT).show();
                break;
            case R.id.inventory:
                startActivity(new Intent(DashboardActivity.this, InventoryActivity.class));
                Toast.makeText(this, "Welocome to Inventory", Toast.LENGTH_SHORT).show();
                break;
            case R.id.teamprofile:
                startActivity(new Intent(DashboardActivity.this, TeamprofileActivity.class));
                Toast.makeText(this, "Welocome to Team Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(DashboardActivity.this, LrAcitivity.class);
                startActivity(logoutIntent);
                Toast.makeText(this, "You Are Loged Out", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        /*--------------------------Hooks----------*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer);
        gridLayout = findViewById(R.id.gridlayout);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        deptchat = findViewById(R.id.deparment);
        teamchat = findViewById(R.id.teamchat1);
        hview = navigationView.getHeaderView(0);
        name = hview.findViewById(R.id.name);
        deptchat.setEnabled(false);
        teamchat.setEnabled(false);

        /*-----------getting User---------------*/
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            email = firebaseUser.getEmail();
        } else {
            startActivity(new Intent(DashboardActivity.this, LrAcitivity.class));

        }

        getUserDept();

        /*---------------------------NevigationDrawer---------------------------------------*/
        nevigationdrawer();
    }


    /*-----------------------Getting UserDepatment--------------------*/
    private void getUserDept() {
        rootRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    deptName = snapshot.child("department").getValue().toString();
                    userName = snapshot.child("name").getValue().toString();
                    name.setText(userName + "\n" + deptName+"\n"+email);
                    deptchat.setEnabled(true);
                    teamchat.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nevigationdrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*---------------------Drawer Item click Handling-------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.Leadership:
                startActivity(new Intent(DashboardActivity.this, LeaderboardActivity.class));
                Toast.makeText(this, "Welcome to LEADERBOARD", Toast.LENGTH_SHORT).show();
                break;

            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(DashboardActivity.this, LrAcitivity.class);
                startActivity(logoutIntent);
                Toast.makeText(this, "You Are Loged Out", Toast.LENGTH_SHORT).show();
                break;
            case R.id.announcement1:
                startActivity(new Intent(DashboardActivity.this, AnnouncementActivity.class));
                Toast.makeText(this, "Welcome to announcement", Toast.LENGTH_SHORT).show();
                break;

            case R.id.teamchat1:
                startActivity(new Intent(DashboardActivity.this, TeamprofileActivity.class));
                Toast.makeText(this, "Welocome to Teamprofile", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}