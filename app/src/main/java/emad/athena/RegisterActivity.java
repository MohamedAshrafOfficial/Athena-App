package emad.athena;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import emad.athena.Model.User;

public class RegisterActivity extends AppCompatActivity {
    // register layout
    EditText nameRegister, mailReg, passwordReg, rePasswordReg, phoneReg;
    RelativeLayout rootRegister;
    Spinner spinnerGender;
    ImageView registerBack;

    String defaultPic = "https://firebasestorage.googleapis.com/v0/b/intellij-4dd19.appspot.com/o/users%2Fdefault.png?alt=media&token=04243d9a-c714-46a4-98bd-6a58f585e13d";
    User user;
    String[] spinnerArray;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    }

    public void initViews() {
        nameRegister = findViewById(R.id.nameRegister);
        mailReg = findViewById(R.id.mailReg);
        passwordReg = findViewById(R.id.passwordReg);
        rePasswordReg = findViewById(R.id.rePasswordReg);
        phoneReg = findViewById(R.id.phoneReg);
        rootRegister = findViewById(R.id.rootRegister);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerArray = getResources().getStringArray(R.array.gender);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(spinnerArrayAdapter);


        registerBack = findViewById(R.id.registerBack);
        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }

    public void login(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    public void register(View view) {

        if (TextUtils.isEmpty(nameRegister.getText().toString().trim())) {
            showSnack("Write Full Name");
        } else if (TextUtils.isEmpty(mailReg.getText().toString().trim())) {
            showSnack("Write your Email");
        } else if (TextUtils.isEmpty(passwordReg.getText().toString().trim())) {
            showSnack("Write your Password");
        } else if (TextUtils.isEmpty(rePasswordReg.getText().toString().trim())) {
            showSnack("Write password again");
        } else if (TextUtils.isEmpty(phoneReg.getText().toString().trim())) {
            showSnack("Write your phone");
        } else {
            if (passwordReg.getText().toString().equals(rePasswordReg.getText().toString())) {
                Log.d(TAG, "onClick: HELLO");

                if (passwordReg.getText().toString().length()>= 8) {


                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("please wait ....");
                    progressDialog.show();

                    // add user to firebase
                    mAuth.createUserWithEmailAndPassword(mailReg.getText().toString(), passwordReg.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        Log.d(TAG, "onComplete: " + currentUser.getUid());
                                        user = new User(currentUser.getUid(), nameRegister.getText().toString(), mailReg.getText().toString(), passwordReg.getText().toString(), phoneReg.getText().toString(), spinnerGender.getSelectedItem().toString(), defaultPic);
                                        usersReference.child(currentUser.getUid()).setValue(user);
                                        addToSharedPreferences();
                                        startActivity(new Intent(RegisterActivity.this, ProfileImageActivity.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWifEmail:failure", task.getException());
                                        progressDialog.dismiss();
                                        showSnack(task.getException().getMessage());
                                    }
                                }
                            });
                }else {
                    showSnack("Password must be more than 8 digits");
                }
            } else {
                showSnack("Passwords not Identical");
            }
        }
    }

    public void showSnack(String text) {
        Snackbar.make(rootRegister, text, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public void addToSharedPreferences() {
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getSharedPreferences("StoreData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("loggedBefore", 1);
        editor.commit();
        editor.apply();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

}
