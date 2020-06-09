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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {

    TextView emaill, uname;
    private FirebaseAuth mAuth;
    private ListView listView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mtoolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseDatabase database;
    ArrayList<String> userList;
    ArrayAdapter<String> adapter;
    DatabaseReference userref, uref;
    String Name, Mobile, Amount, Date_of_Journey, Departure_City, Email, Number_of_Tourists, Rooms_Needed, Selected_Tour;
    public String nameee;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        user = new User();
        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Admin");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(AdminHomeActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView)findViewById(R.id.nav_v);
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
        database = FirebaseDatabase.getInstance();
        userref = database.getReference().child("Userinfo");
        listView = (ListView) findViewById(R.id.home_listview);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.user_info, R.id.userinfo_ui, userList);

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    user = ds.getValue(User.class);
                    nameee = ds.getKey();
                    userList.add("User Information:"+"\n"+"\n"+"Name: "+user.getName().toString()+"\n"+"Email: "+user.getEmail().toString()+"\n"+"Address: "+user.getAddress()+
                    "\n"+"Mobile: "+user.getMobile().toString()+"\n"+"Gender: "+user.getGender().toString()+"\n"+"DOB: "+user.getDOB().toString()+"\n"+
                            "Occupation: "+user.getOccupation().toString()+"\n"+"Contact Form: "+user.getContact_Form().toString()
                    );
                    uref = userref.child(nameee).child("Booking");
                    uref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if(dataSnapshot.hasChild("Email")){
                                    Email = dataSnapshot.child("Email").getValue().toString();
                                    Name = dataSnapshot.child("Name").getValue(String.class);
                                    Departure_City = dataSnapshot.child("Departure_City").getValue(String.class);
                                    Mobile = dataSnapshot.child("Mobile").getValue(String.class);
                                    Amount = dataSnapshot.child("Amount").getValue().toString();
                                    Date_of_Journey = dataSnapshot.child("Date_of_Journey").getValue().toString();
                                    Number_of_Tourists = dataSnapshot.child("Number_of_Tourists").getValue().toString();
                                    Rooms_Needed = dataSnapshot.child("Rooms_Needed").getValue().toString();
                                    Selected_Tour = dataSnapshot.child("Selected_Tour").getValue(String.class);
                                    userList.add("Booking:" + "\n" + "\n" + "Name: " + Name + "\n" + "Email: " + Email + "\n" + "Departure City: " + Departure_City +
                                            "\n" + "Mobile: " + Mobile + "\n" + "Date of Journey: " + Date_of_Journey + "\n" + "Number of Tourists: " +
                                            Number_of_Tourists + "\n" + "Rooms Needed: " + Rooms_Needed + "\n" + "Amount: " + Amount +
                                            "\n" + "Selected Tour: " + Selected_Tour);

                                }

                            }
                            listView.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        SetInfo();
        Menu navmenuu = navigationView.getMenu();
        navmenuu.findItem(R.id.nav_signin).setVisible(false);
        navmenuu.findItem(R.id.nav_signup).setVisible(false);
        navmenuu.findItem(R.id.nav_admin).setVisible(false);
        navmenuu.findItem(R.id.nav_home).setVisible(false);
        navmenuu.findItem(R.id.nav_contactus).setVisible(false);
        navmenuu.findItem(R.id.nav_settings).setVisible(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

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
            case R.id.nav_logout:
                mAuth.signOut();
                SendToHome();
                Toast.makeText(this, "Admin has been signed out", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendToHome() {
        Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SetInfo(){
        uname.setText("Hello, Admin");
        emaill.setText("Email: " + "admin@travio.com");
    }

}
