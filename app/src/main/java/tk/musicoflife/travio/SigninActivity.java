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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    private TextView needaccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        email = (EditText)findViewById(R.id.login_email);
        password = (EditText)findViewById(R.id.login_password);
        login = (Button)findViewById(R.id.login_button);
        needaccount = (TextView)findViewById(R.id.login_need_account);

        needaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowToLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = mAuth.getCurrentUser();
        if(current_user != null){
            sendToHome();
        }
    }

    private void allowToLogin() {
        String EMAIL = email.getText().toString();
        String PASSWORD = password.getText().toString();
        if(TextUtils.isEmpty(EMAIL)){
            Toast.makeText(this, "Please enter your E-mail...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(PASSWORD)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Signing you in");
            loadingbar.setMessage("Please wait...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(EMAIL, PASSWORD)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendToHome();
                                Toast.makeText(SigninActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }
                            else {
                                String msg = task.getException().getMessage();
                                loadingbar.dismiss();
                                Toast.makeText(SigninActivity.this, "Error occurred: " + msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    private void sendToHome() {
        Intent in = new Intent(this, HomeActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    private void sendToRegister() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }
}