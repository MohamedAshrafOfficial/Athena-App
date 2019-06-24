package emad.athena;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import emad.athena.Adapters.GuiderAdapter;
import emad.athena.Model.Guider;

public class GuiderActivity extends AppCompatActivity {

    ImageView imgBackGuider;
    GuiderAdapter adapter;
    ArrayList<Guider> guiderArrayList = new ArrayList<>();
    Guider guider= new Guider();
    RecyclerView guiderRecycler;

    FirebaseAuth mAuth;
    DatabaseReference guiderReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guider);

        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        guiderReference = FirebaseDatabase.getInstance().getReference().child("guider");
        guiderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guiderArrayList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    guider = snapshot.getValue(Guider.class);
                    guiderArrayList.add(guider);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void initRecyclerView(){
        imgBackGuider = findViewById(R.id.imgBackGuider);
        imgBackGuider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        guiderRecycler = findViewById(R.id.guiderRecycler);
        guiderRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuiderAdapter(guiderArrayList, this);
        guiderRecycler.setAdapter(adapter);
    }
}
