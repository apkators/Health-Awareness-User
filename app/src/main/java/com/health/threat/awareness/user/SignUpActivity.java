package com.health.threat.awareness.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.health.threat.awareness.user.app.AppPreferenceManager;
import com.health.threat.awareness.user.model.AppUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    String Email = "", id, Contact_Number, Password = "", Confirm_password = "", Name = "";
    EditText editTextEmail, editTextContact_No, editTextPassword, editTextConfirmPassword, editTextName;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference database, subRef;//, deviceTokenRef;
    TextView SignUpButton;
    TextView loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        //deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("DeviceTokens");

        initializeItems();

        editTextContact_No.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+92")) {
                    editTextContact_No.setText("+92");
                    Selection.setSelection(editTextContact_No.getText(), editTextContact_No.getText().length());
                }

            }
        });
        editTextContact_No.setText("+92");
        Selection.setSelection(editTextContact_No.getText(), editTextContact_No.getText().length());

        SignUpButton.setOnClickListener(v -> createuser());
        loginActivity.setOnClickListener(v -> StartLoginActivity());
    }

    private void StartLoginActivity() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        this.finish();
    }

    public void initializeItems() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextContact_No = findViewById(R.id.editTextContact_No);
        SignUpButton = findViewById(R.id.btnSignUp);
        loginActivity = findViewById(R.id.loginActivity);

        pd = new ProgressDialog(this);
    }

    public void createuser() {
        pd.setTitle("Registering User Please Wait...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);
        Email = editTextEmail.getText().toString().trim();
        Password = editTextPassword.getText().toString().trim();
        Name = editTextName.getText().toString().trim();
        Contact_Number = editTextContact_No.getText().toString().trim();
        Confirm_password = editTextConfirmPassword.getText().toString().trim();

        String m = editTextEmail.getText().toString();
        String p = editTextPassword.getText().toString();

        if (!validation()) {
            pd.dismiss();
            return;
        }

        mAuth.createUserWithEmailAndPassword(m, p).addOnSuccessListener(SignUpActivity.this, authResult -> {
            Toast.makeText(SignUpActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            id = Objects.requireNonNull(currentUser).getUid();
            writedata();
        }).addOnFailureListener(e -> {
            Toast.makeText(SignUpActivity.this, "Email Already In Use", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        });

    }

    public void writedata() {
        subRef = database.child(id);
        AppUser u = new AppUser(Email, id, Contact_Number, Name);
        subRef.child("Email").setValue(u.getEmail());
        subRef.child("Name").setValue(u.getName());
        subRef.child("Id").setValue(u.getId());
        subRef.child("Mobile").setValue(u.getMobile());

        String token = new AppPreferenceManager(SignUpActivity.this).getDeviceToken();
        DatabaseReference deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("UsersDeviceTokens").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        deviceTokenRef.child("DeviceToken").setValue(token);

        startActivity(new Intent(SignUpActivity.this, LocationActivity.class));
        if (pd.isShowing())
            pd.dismiss();
        finish();
    }

    private boolean validation() {
        if (Name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return false;
        }
        if (Email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            editTextEmail.setError("Please Enter valid Email address");
            editTextEmail.requestFocus();
            return false;
        }
        if (Password.isEmpty()) {
            editTextPassword.setError("Please Enter the password");
            editTextPassword.requestFocus();
            return false;
        }
        if (Password.length() < 8) {
            editTextPassword.setError("Minimum password length is 6");
            editTextPassword.requestFocus();
            return false;
        }
        if (!Password.equals(Confirm_password)) {
            editTextPassword.setError("Password did not match with Confirm Password");
            editTextPassword.requestFocus();
            return false;
        }
        if (Contact_Number.isEmpty()) {
            editTextContact_No.setError("Enter Mobile Number");
            editTextContact_No.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        this.finish();
    }
}