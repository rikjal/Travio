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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private Button btncreate;
    private EditText email, password, confirm, name;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private DatabaseReference userref, uref;

    String Name;
    String currentID;
    String e_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        loadingbar = new ProgressDialog(this);
        btncreate = (Button)findViewById(R.id.register_button);
        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_password);
        confirm = (EditText)findViewById(R.id.register_confirm);
        name = (EditText)findViewById(R.id.register_name);

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }

    private void CreateNewAccount() {
        e_mail = email.getText().toString();
        String Password = password.getText().toString();
        String Conf = confirm.getText().toString();
        Name = name.getText().toString();

        if(TextUtils.isEmpty(e_mail)){
            Toast.makeText(this, "Please enter your E-mail ID...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Conf)){
            Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Please enter your full name...", Toast.LENGTH_SHORT).show();
        }
        else if(!Password.equals(Conf)){
            Toast.makeText(this, "Your passwords don't match!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Creating new account");
            loadingbar.setMessage("Please wait while we create your new account...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(e_mail, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                addName();
                                sendToMain();
                                Toast.makeText(SignupActivity.this, "Your account is created successfully!", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(SignupActivity.this, "Error occurred: " + msg, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void addName() {
        currentID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        uref = FirebaseDatabase.getInstance().getReference().child("Userinfo").child(Name);
        HashMap userMap = new HashMap();
        userMap.put("Name", Name);
        userMap.put("Email", e_mail);

        userref.updateChildren(userMap);
        uref.updateChildren(userMap);
    }

    private void sendToMain() {
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}