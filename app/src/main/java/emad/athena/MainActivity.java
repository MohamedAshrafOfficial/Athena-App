package emad.athena;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import emad.athena.Adapters.HomePagerAdapter;
import emad.athena.Fragments.FeautresFragment;
import emad.athena.Fragments.HomeFragment;
import emad.athena.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ImageView moreMenu;
    TextView txHome;
    TextView txFeautres;
    TextView txProfile;
    ViewPager homePager;
    HomePagerAdapter homePagerAdapter;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        handleViews();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (mAuth.getCurrentUser()==null)
//            startActivity(new Intent(this, LoginActivity.class));
//
//    }

    public void initViews(){
        moreMenu = findViewById(R.id.moreMenu);
        txHome = findViewById(R.id.txHome);
        txFeautres = findViewById(R.id.txFeautres);
        txProfile = findViewById(R.id.txProfile);
        homePager = findViewById(R.id.homePager);
    }

    public void handleViews(){
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePager.setAdapter(homePagerAdapter);
        homePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                changeTabs(i);
            }

            @Override
            public void onPageSelected(int i) {
                changeTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        txHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePager.setCurrentItem(0);
            }
        });
        txFeautres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePager.setCurrentItem(1);
            }
        });
        txProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePager.setCurrentItem(2);
            }
        });
        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePager.setCurrentItem(3);
            }
        });
    }

    public void changeTabs(int position){
        if (position==0){
            txHome.setTextColor(getResources().getColor(R.color.LightYellow));
            txHome.setTextSize(22);

            txFeautres.setTextColor(getResources().getColor(R.color.Linen));
            txFeautres.setTextSize(18);

            txProfile.setTextColor(getResources().getColor(R.color.Linen));
            txProfile.setTextSize(18);

            moreMenu.setImageResource(R.drawable.menu_linun);

        } else if (position==1){
            txHome.setTextColor(getResources().getColor(R.color.Linen));
            txHome.setTextSize(18);

            txFeautres.setTextColor(getResources().getColor(R.color.LightYellow));
            txFeautres.setTextSize(22);

            txProfile.setTextColor(getResources().getColor(R.color.Linen));
            txProfile.setTextSize(18);

            moreMenu.setImageResource(R.drawable.menu_linun);
        }
        else if (position==2){
            txHome.setTextColor(getResources().getColor(R.color.Linen));
            txHome.setTextSize(18);

            txFeautres.setTextColor(getResources().getColor(R.color.Linen));
            txFeautres.setTextSize(18);

            txProfile.setTextColor(getResources().getColor(R.color.LightYellow));
            txProfile.setTextSize(22);
            moreMenu.setImageResource(R.drawable.menu_linun);

        }else if (position==3){
            txHome.setTextColor(getResources().getColor(R.color.Linen));
            txHome.setTextSize(18);

            txFeautres.setTextColor(getResources().getColor(R.color.Linen));
            txFeautres.setTextSize(18);

            txProfile.setTextColor(getResources().getColor(R.color.LightYellow));
            txProfile.setTextSize(18);

            moreMenu.setImageResource(R.drawable.menu_yelli);


        }
    }
}
