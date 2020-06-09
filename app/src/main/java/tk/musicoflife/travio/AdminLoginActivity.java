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
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, pass;
    private Button btn;
    private ProgressDialog loadingbar;
    private FirebaseUser currentuser;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        email = (EditText)findViewById(R.id.admin_login_email);
        pass = (EditText)findViewById(R.id.admin_login_password);
        btn = (Button)findViewById(R.id.admin_login_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allow();
            }
        });

    }

    private void allow() {
        String EMAIL = email.getText().toString();
        String PASSWORD = pass.getText().toString();
        if(TextUtils.isEmpty(EMAIL)){
            Toast.makeText(this, "Please enter your E-mail...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(PASSWORD)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else if(EMAIL.equals("admin@travio.com")){
            loadingbar.setTitle("Signing you in");
            loadingbar.setMessage("Please wait...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(EMAIL, PASSWORD)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                SendToAdminHome();
                                Toast.makeText(AdminLoginActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }
                            else {
                                String msg = task.getException().getMessage();
                                loadingbar.dismiss();
                                Toast.makeText(AdminLoginActivity.this, "Error occurred: " + msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "You are not an Admin!", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendToAdminHome() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
