package emad.athena.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import emad.athena.FeedbackActivity;
import emad.athena.GuiderActivity;
import emad.athena.LoginActivity;
import emad.athena.R;
import emad.athena.RecentActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {
    RelativeLayout RecentLayout, guideLayout, feedbackLayout,signoutLayout;

    FirebaseAuth mAuth;
    public MoreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_more, container, false);
        mAuth = FirebaseAuth.getInstance();
        initViews(view);
        handleViews();


        return view;
    }

    public void initViews(View view){
        RecentLayout = view.findViewById(R.id.RecentLayout);
        guideLayout = view.findViewById(R.id.guideLayout);
        feedbackLayout = view.findViewById(R.id.feedbackLayout);
        signoutLayout = view.findViewById(R.id.signoutLayout);
    }

    public void handleViews(){
        RecentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getActivity(), RecentActivity.class));
            }
        });

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getActivity(), FeedbackActivity.class));

            }
        });

        signoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        guideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GuiderActivity.class));
            }
        });
    }
}
