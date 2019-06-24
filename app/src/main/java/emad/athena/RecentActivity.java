package emad.athena;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import emad.athena.Adapters.RecentAdapter;
import emad.athena.Model.Recent;

public class RecentActivity extends AppCompatActivity {

    ImageView imgBackRecent;
    TextView empty_view;
    RecyclerView recentRecycler;
    LinearLayout clearRecent;
    ArrayList<Recent> recentList = new ArrayList<>();
    RecentAdapter recentAdapter;
    DatabaseReference recentReference;
    FirebaseAuth mAuth;
    private static final String TAG = "RecentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        initRecyclerView();
        handleClear();
    }
    @Override
    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        recentReference = FirebaseDatabase.getInstance().getReference().child("Recent").child(mAuth.getCurrentUser().getUid());
        recentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recentList.clear();
                Recent recent;
                for (DataSnapshot value : dataSnapshot.getChildren()){
                    recent = value.getValue(Recent.class);
                    recentList.add(recent);
                }
                if (recentList.size()>1)
                    recentRecycler.smoothScrollToPosition(recentRecycler.getAdapter().getItemCount()-1);

                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initRecyclerView(){
        imgBackRecent = findViewById(R.id.imgBackRecent);
        imgBackRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        empty_view = findViewById(R.id.empty_view);
        clearRecent = findViewById(R.id.clearRecent);
        recentRecycler = findViewById(R.id.recentRecycler);
        recentRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentAdapter = new RecentAdapter(this,recentList);
        recentRecycler.setAdapter(recentAdapter);
    }

    public void handleClear(){
        clearRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentReference = FirebaseDatabase.getInstance().getReference().child("Recent").child(mAuth.getCurrentUser().getUid());
                recentReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d(TAG, "onComplete:deleted ");
                        else
                            Log.d(TAG, "onComplete:failed ");
                    }
                });
            }
        });
    }
}

