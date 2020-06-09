package tk.musicoflife.travio;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    Spinner spinner, spinner3;
    ArrayAdapter<CharSequence> adapter, adapter3;
    private EditText dob, address, mobile;
    private Button datebtn, submitbutton;
    static final int DIALOG_ID =0;
    int yearx, dayx, monthx;
    private FirebaseAuth mAuth;
    private DatabaseReference userref, uref, ur;
    private ProgressDialog loadingbar;
    String gend, dept, namee;
    String currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadingbar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        uref = FirebaseDatabase.getInstance().getReference().child("Userinfo");
        final Calendar cal = Calendar.getInstance();
        yearx = cal.get(Calendar.YEAR);
        monthx = cal.get(Calendar.MONTH);
        dayx = cal.get(Calendar.DAY_OF_MONTH);

        address = (EditText)findViewById(R.id.setup_address);
        mobile = (EditText)findViewById(R.id.setup_mobile);

        dob = (EditText)findViewById(R.id.setup_date);
        datebtn = (Button)findViewById(R.id.setup_date_btn);
        submitbutton = (Button)findViewById(R.id.setup_submit);
        spinner = (Spinner)findViewById(R.id.spinnergen);
        spinner3 =(Spinner)findViewById(R.id.spinner_department);
        adapter3 = ArrayAdapter.createFromResource(this,R.array.department, R.layout.support_simple_spinner_dropdown_item);
        adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.support_simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner3.setAdapter(adapter3);
        showDialogonbtn();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gend = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dept = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInformation();
            }
        });
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Name")){
                        namee = dataSnapshot.child("Name").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("Address")){
                        String aaddress = dataSnapshot.child("Address").getValue().toString();
                        String date = dataSnapshot.child("DOB").getValue().toString();
                        String mmobile = dataSnapshot.child("Mobile").getValue().toString();
                        String ggender = dataSnapshot.child("Gender").getValue().toString();
                        String depart = dataSnapshot.child("Occupation").getValue().toString();

                        dob.setText(date);
                        address.setText(aaddress);
                        mobile.setText(mmobile);
                        switch (ggender){
                            case "Male":
                                spinner.setSelection(0);
                                break;
                            case "Female":
                                spinner.setSelection(1);
                                break;
                        }
                        switch (depart){
                            case "Student":
                                spinner3.setSelection(0);
                                break;
                            case "Job":
                                spinner3.setSelection(1);
                                break;
                            case "Business":
                                spinner3.setSelection(2);
                                break;
                            case "None":
                                spinner3.setSelection(3);
                                break;
                        }
                    }
                    else {
                        dob.setText("");
                        address.setText("");
                        mobile.setText("");

                        Toast.makeText(SettingsActivity.this, "Nothing is there in our Database!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SaveInformation() {
        String mobileno = mobile.getText().toString();
        String addresss = address.getText().toString();
        String dateofb = dob.getText().toString();

        if(TextUtils.isEmpty(mobileno)){
            Toast.makeText(this, "Please enter your Mobile number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addresss)){
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(dateofb)){
            Toast.makeText(this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Updating your profile");
            loadingbar.setMessage("Please wait while we update your profile...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            HashMap usermap = new HashMap();
            usermap.put("Mobile", mobileno);
            usermap.put("Address", addresss);
            usermap.put("DOB", dateofb);
            usermap.put("Occupation", dept);
            usermap.put("Gender", gend);
            userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendToMain();
                        Toast.makeText(SettingsActivity.this, "Account information updated successfully!", Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                    else {
                        String mesg = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Error occurred: " + mesg, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });
            ur = uref.child(namee);
            ur.updateChildren(usermap);
        }
    }

    private void sendToMain() {
        Intent in = new Intent(SettingsActivity.this, HomeActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    public void showDialogonbtn(){
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int ID){
        if(ID == DIALOG_ID)
            return new DatePickerDialog(this, dpListener, yearx, monthx, dayx);
        return null;
    }
    private DatePickerDialog.OnDateSetListener dpListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yearx = year;
            monthx = month+1;
            dayx = dayOfMonth;
            String dateofbirth = dayx+"/"+monthx+"/"+yearx;
            dob.setText(dateofbirth);
        }
    };
}
