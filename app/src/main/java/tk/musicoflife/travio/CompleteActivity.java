package tk.musicoflife.travio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompleteActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    String currentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        btn = (Button)findViewById(R.id.complete_button);
        tv = (TextView)findViewById(R.id.complete_details);
        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID).child("Booking");

        SetData();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToHome();
            }
        });
    }

    private void SetData() {
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Rooms_Needed")){
                        String rooms = dataSnapshot.child("Rooms_Needed").getValue().toString();
                        String numoftourists = dataSnapshot.child("Number_of_Tourists").getValue().toString();
                        String mmobile = dataSnapshot.child("Mobile").getValue().toString();
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String email = dataSnapshot.child("Email").getValue().toString();
                        String depcity = dataSnapshot.child("Departure_City").getValue().toString();
                        String dateofjourney = dataSnapshot.child("Date_of_Journey").getValue().toString();
                        String selectedtour = dataSnapshot.child("Selected_Tour").getValue().toString();
                        String amount = dataSnapshot.child("Amount").getValue().toString();

                        tv.setText("Name: " + name + "\n" + "Email: " + email + "Mobile: " + mmobile + "\n" + "Your Tour: " + selectedtour + "\n" +
                                "Date of Journey: "+ dateofjourney + "\n" +
                        "Departure City: " + depcity + "\n" + "Number of Tourists: " + numoftourists + "\n" + "Number of Rooms: " + rooms
                        + "\n" + "Amount: " + amount + "/- Only(Per Person)");
                    }
                    else {
                        tv.setText("");
                        Toast.makeText(CompleteActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendToHome() {
        Intent intent = new Intent(CompleteActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
