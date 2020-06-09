package tk.musicoflife.travio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TamilnaruActivity extends AppCompatActivity {

    private Button btn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamilnaru);
        btn = (Button)findViewById(R.id.tn_booknow);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser == null){
                    SendToSignin();
                    Toast.makeText(TamilnaruActivity.this, "Please sign in to continue", Toast.LENGTH_LONG).show();
                }
                else {
                    SendToBooking();
                }
            }
        });
    }

    private void SendToSignin() {
        Intent i = new Intent(TamilnaruActivity.this, SigninActivity.class);
        startActivity(i);
    }

    private void SendToBooking() {
        Intent intent = new Intent(TamilnaruActivity.this, BookingActivity.class);
        startActivity(intent);
    }
}
