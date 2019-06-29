package emad.athena;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFirstActivty extends AppCompatActivity {

    AutoCompleteTextView emailReset;
    Button btnReset;
    TextView tvGoBack;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_first_activty);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        initViews();
        handleViews();
    }

    public void initViews(){
        emailReset = findViewById(R.id.atvEmailRes);
        btnReset = findViewById(R.id.btnReset);
        tvGoBack = findViewById(R.id.tvGoBack);
    }

    public void handleViews(){
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailReset.getText().toString().trim().equals("")){

                    mAuth.sendPasswordResetEmail(emailReset.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Snackbar.make(findViewById(android.R.id.content), "Check your mail ", Snackbar.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordFirstActivty.this, LoginActivity.class));
                            }
                            else
                                Snackbar.make(findViewById(android.R.id.content), "Try Later", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Snackbar.make(findViewById(android.R.id.content), "Write your Email", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordFirstActivty.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}