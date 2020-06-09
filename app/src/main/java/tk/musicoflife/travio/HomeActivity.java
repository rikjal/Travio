package tk.musicoflife.travio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class HomeActivity extends AppCompatActivity {

    List<Integer> l = new ArrayList<>(0);

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mtoolbar;
    private Button keralabutton;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private TextView uname, emaill;
    private DatabaseReference userref;
    String currentID, username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
        currentID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        }

        keralabutton = (Button)findViewById(R.id.home_btn_kerala);
        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_lay);
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView = (NavigationView)findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        uname = (TextView)navView.findViewById(R.id.nav_username);
        emaill = (TextView)navView.findViewById(R.id.nav_emailaddress);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                usermenuselector(item);
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser != null) {
            if (currentID.equals("ay4POYHCCnbo1bXyvpjNTdJuAIk2")) {
                SendToAdminHome();
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_LONG).show();
            }
            else{
                HideSigninSignup();
                SetUserInfo();
            }
        }
        else {
            HideLogout();
            SetUserInfotoNone();
        }

    }

    private void SendToAdminHome() {
        Intent intent = new Intent(HomeActivity.this, AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void SetUserInfo() {
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Name")) {
                    username = dataSnapshot.child("Name").getValue().toString();
                    uname.setText("Hi, " + username);
                }
                    if(dataSnapshot.hasChild("Email")){
                    email = dataSnapshot.child("Email").getValue().toString();
                    emaill.setText("Email: " + email);

                }
                else {
                    Toast.makeText(HomeActivity.this, "No data exists!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SetUserInfotoNone() {
        uname.setText("User isn't Signed in");
        emaill.setText("");
    }

    private void HideSigninSignup() {
        Menu navmenuu = navigationView.getMenu();
        navmenuu.findItem(R.id.nav_signin).setVisible(false);
        navmenuu.findItem(R.id.nav_signup).setVisible(false);
        navmenuu.findItem(R.id.nav_admin).setVisible(false);
    }

    private void HideLogout() {
        Menu navmenu = navigationView.getMenu();
        navmenu.findItem(R.id.nav_settings).setVisible(false);
        navmenu.findItem(R.id.nav_logout).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void usermenuselector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                SendToHome();
                break;
            case R.id.nav_signin:
                SendToSignin();
                break;
            case R.id.nav_admin:
                SendToAdmin();
                break;
            case R.id.nav_signup:
                SendToSignup();
                break;
            case R.id.nav_contactus:
                SendToContact();
                break;
            case R.id.nav_settings:
                SendToSettings();
                break;
            case R.id.nav_logout:
                Logout();
                break;
        }
    }

    private void SendToAdmin() {
        Intent intent = new Intent(HomeActivity.this, AdminLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Logout() {
        mAuth.signOut();
        SendToHome();
        Toast.makeText(this, "Signed Out Successfully.", Toast.LENGTH_LONG).show();
    }

    private void SendToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void SendToContact() {
        if(firebaseUser != null) {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Please sign up/sign in to continue.", Toast.LENGTH_LONG).show();
        }
    }

    private void SendToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void SendToSignin() {
        Intent i = new Intent(this, SigninActivity.class);
        startActivity(i);
    }

    private void SendToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void details(View view){
        switch (view.getId()){
            case R.id.home_btn_tn:
                Intent intent = new Intent(HomeActivity.this, TamilnaruActivity.class);
                startActivity(intent);
                break;
            case R.id.home_btn_goa:
                Intent intent1 = new Intent(HomeActivity.this, GoaActivity.class);
                startActivity(intent1);
                break;
            case R.id.home_btn_kerala:
                Intent intent2 = new Intent(HomeActivity.this, KeralaActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
