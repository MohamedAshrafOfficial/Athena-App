package emad.athena.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import emad.athena.Model.User;
import emad.athena.R;

public class ProfileFragment extends Fragment {
    de.hdodenhof.circleimageview.CircleImageView viewProfileImage;
    TextView viewUserName;
    TextView viewMail;
    TextView viewGender;
    TextView viewPhone;

    User currentUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private static final String TAG = "ViewProfileActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());


        viewProfileImage = view.findViewById(R.id.viewProfileImage);
        viewUserName = view.findViewById(R.id.viewUserName);
        viewMail = view.findViewById(R.id.viewMail);
        viewGender = view.findViewById(R.id.viewGender);
        viewPhone = view.findViewById(R.id.viewPhone);

        setupViews();
        return view;
    }


    public void setupViews(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);

                Picasso.get().load(currentUser.getPictureURL()).placeholder(R.drawable.defaultpro).into(viewProfileImage);

                viewUserName.setText(currentUser.getName());
                viewMail.setText(currentUser.getMail());
                viewGender.setText(currentUser.getGender());
                viewPhone.setText(currentUser.getPhone());

                Log.d(TAG, "onDataChange: " +currentUser.getName() );
                Log.d(TAG, "onDataChange: " +currentUser.getPhone() );
                Log.d(TAG, "onDataChange: " + currentUser.getGender());
                Log.d(TAG, "onDataChange: " + currentUser.getMail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: PROFILE");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: PROFILE");
    }
}
