package tk.musicoflife.travio;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import java.util.Calendar;
import java.util.HashMap;

public class BookingActivity extends AppCompatActivity {
    Spinner spin, spin2;
    ArrayAdapter<CharSequence> adapter, adapter2;
    private Button datebtn, submitbutton;
    int yearx, dayx, monthx;
    static final int DIALOG_ID =0;
    private EditText rooms, numbers, name, contact, email, dateofjourney;
    private FirebaseAuth mAuth;
    private DatabaseReference userref, uref, urr, ur;
    private ProgressDialog loadingbar;
    String currentID, cityy, tour, namee;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID).child("Booking");
        uref = FirebaseDatabase.getInstance().getReference().child("Userinfo");
        urr = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        loadingbar = new ProgressDialog(this);
        final Calendar cal = Calendar.getInstance();
        yearx = cal.get(Calendar.YEAR);
        monthx = cal.get(Calendar.MONTH);
        dayx = cal.get(Calendar.DAY_OF_MONTH);

        rooms = (EditText)findViewById(R.id.booking_room);
        numbers = (EditText)findViewById(R.id.booking_numbers);
        name = (EditText)findViewById(R.id.booking_name);
        contact = (EditText)findViewById(R.id.booking_contactno);
        email = (EditText)findViewById(R.id.booking_email);
        dateofjourney = (EditText)findViewById(R.id.booking_date);
        datebtn = (Button)findViewById(R.id.booking_date_btn);
        submitbutton = (Button)findViewById(R.id.booking_btn);
        spin = (Spinner)findViewById(R.id.spinnercity);
        spin2 = (Spinner)findViewById(R.id.spinnerplace);
        adapter = ArrayAdapter.createFromResource(this, R.array.city, R.layout.support_simple_spinner_dropdown_item);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.place, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin2.setAdapter(adapter2);
        showDialogonbtn();
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityy = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tour = (String) parent.getItemAtPosition(position);
                switch (tour){
                    case "Economy Tamil Nadu - Pondicherry and Tirupati":
                        amount = 21599;
                        break;
                    case "Kerala - Hills and Backwaters":
                        amount = 13599;
                        break;
                    case "Vibrant Goa - Evoke Lifestyle":
                        amount = 11299;
                        break;
                }
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
        urr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Name")){
                        namee = dataSnapshot.child("Name").getValue().toString();
                        ur = uref.child(namee).child("Booking");
                    }
                    else {
                        Toast.makeText(BookingActivity.this, "No name exists!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(BookingActivity.this, "Error in database!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void SaveInformation() {
        String roomsneeded = rooms.getText().toString();
        String numbersoftourists = numbers.getText().toString();
        String fullname = name.getText().toString();
        String mobileno = contact.getText().toString();
        String emailaddress = email.getText().toString();
        String journeydate = dateofjourney.getText().toString();

        if(TextUtils.isEmpty(roomsneeded)){
            Toast.makeText(this, "Please enter the number of rooms needed", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(numbersoftourists)){
            Toast.makeText(this, "Please enter the number of persons", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(mobileno)){
            Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(emailaddress)){
            Toast.makeText(this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(journeydate)){
            Toast.makeText(this, "Please enter the date of journey", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Confirming your booking");
            loadingbar.setMessage("Please wait while we confirm your booking with us...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            HashMap usermap = new HashMap();
            usermap.put("Rooms_Needed", roomsneeded);
            usermap.put("Number_of_Tourists", numbersoftourists);
            usermap.put("Name", fullname);
            usermap.put("Mobile", mobileno);
            usermap.put("Email", emailaddress);
            usermap.put("Date_of_Journey", journeydate);
            usermap.put("Departure_City", cityy);
            usermap.put("Selected_Tour", tour);
            usermap.put("Amount", amount);
            userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SendToComplete();
                        loadingbar.dismiss();
                        Toast.makeText(BookingActivity.this, "Your booking was successfully done!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String mesg = task.getException().getMessage();
                        Toast.makeText(BookingActivity.this, "Error occurred: " + mesg, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });
            ur.updateChildren(usermap);
        }
    }

    private void SendToComplete() {
        Intent in = new Intent(BookingActivity.this, CompleteActivity.class);
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
            dateofjourney.setText(dateofbirth);
        }
    };
}
