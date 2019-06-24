package emad.athena;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import emad.athena.Model.Feedback;

public class FeedbackActivity extends AppCompatActivity {

    ImageView imgBackFeedback;
    EditText feedbackText;
    Button btnSendFeedback;
    private DatabaseReference feedbackReference;
    FirebaseAuth mAuth;
    private static final String TAG = "FeedbackActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        handleViews();

    }

    public void initViews(){
        imgBackFeedback = findViewById(R.id.imgBackFeedback);
        btnSendFeedback = findViewById(R.id.btnSendFeedback);
        feedbackText = findViewById(R.id.feedbackText);
    }


    public void handleViews(){
        imgBackFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feedbackText.getText().toString().trim().equals(""))
                    sendFeedback(feedbackText.getText().toString().trim());
            }
        });
    }

    public void sendFeedback(String mFeedback) {
        feedbackReference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(mAuth.getCurrentUser().getUid()).push();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        Feedback feedback = new Feedback(mFeedback, timeStamp);
        feedbackReference.setValue(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: uploading feedback succeeded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: uploading feedback failed");
            }
        });
    }
}
