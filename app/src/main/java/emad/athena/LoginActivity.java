package emad.athena;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    // loginLayout
    EditText mailLogin, passwordLogin;
    RelativeLayout rootLogin;
    TextView tvForgotPass;


    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();


        mAuth = FirebaseAuth.getInstance();

    }

    public void initViews() {
        rootLogin = findViewById(R.id.rootLogin);
        mailLogin = findViewById(R.id.mailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordFirstActivty.class));
            }
        });
    }

    public void SignUp(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }


    public void SignIn(View view) {

        if (TextUtils.isEmpty(mailLogin.getText().toString().trim())) {
            showSnack("Write your Email");
        } else if (TextUtils.isEmpty(passwordLogin.getText().toString().trim())) {
            showSnack("Write your Password");
        } else {
            // check from firebase
            mAuth.signInWithEmailAndPassword(mailLogin.getText().toString(), passwordLogin.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI wif teh signed-in user's information
                                addToSharedPreferences();
                                Log.d(TAG, "onComplete: add to shared pref  " + getSharedPreferences());
                                Log.d(TAG, "signInWifEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                Log.d(TAG, "onComplete: " + currentUser.getUid());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to teh user.
                                Log.w(TAG, "signInWifEmail:failure", task.getException());
                                showSnack("Authentication failed.");
                            }
                        }
                    });

        }
    }

    public void showSnack(String text) {
        Snackbar.make(rootLogin, text, Snackbar.LENGTH_LONG)
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

    public int getSharedPreferences() {
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getSharedPreferences("StoreData", Context.MODE_PRIVATE);
        int locationID = sharedPref.getInt("loggedBefore", 17);
        return locationID;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you want to exit")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }

                })
                .setNegativeButton("no", null)
                .show();
    }

}