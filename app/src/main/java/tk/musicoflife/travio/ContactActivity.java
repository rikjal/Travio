package tk.musicoflife.travio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText et;
    private Button bt;
    String currentID, namee;
    private DatabaseReference userref, uref, ur;
    private ProgressDialog loadingbar;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        loadingbar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        uref = FirebaseDatabase.getInstance().getReference().child("Userinfo");
        et = (EditText)findViewById(R.id.contact_formtext);
        bt = (Button)findViewById(R.id.contact_send);

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    namee = dataSnapshot.child("Name").getValue().toString();
                    ur = uref.child(namee);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = et.getText().toString();
                if(TextUtils.isEmpty(data)){
                    Toast.makeText(ContactActivity.this, "Please enter your message first.", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendInfo();
                }
            }
        });
    }

    private void sendInfo() {
        String data = et.getText().toString();
        loadingbar.setTitle("Sending your message");
        loadingbar.setMessage("Please wait while the message is being sent...");
        loadingbar.show();
        loadingbar.setCanceledOnTouchOutside(true);
        HashMap map = new HashMap();
        map.put("Contact_Form", data);
        userref.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendToHome();
                    loadingbar.dismiss();
                    Toast.makeText(ContactActivity.this, "Your query was logged successfully!", Toast.LENGTH_LONG).show();
                }
                else {
                    String mesg = task.getException().getMessage();
                    Toast.makeText(ContactActivity.this, "Error occurred: " + mesg, Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }
        });
        ur.updateChildren(map);
    }

    private void SendToHome() {
        Intent intent = new Intent(ContactActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
