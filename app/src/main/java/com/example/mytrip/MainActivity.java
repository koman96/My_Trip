package com.example.mytrip;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check_authority();
    }

    private void check_authority() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (user == null) {
            SignFragment signFragment = new SignFragment();
            transaction.replace(R.id.main_fragment, signFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {
            Intent intent = getIntent();

            if (! intent.hasExtra("tripName") ) {
                MainFragment mainFragment = new MainFragment();
                transaction.replace(R.id.main_fragment, mainFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            else {
                String tripName = intent.getStringExtra("tripName");
                double startLat = intent.getDoubleExtra("startLat" ,0);
                double startLon = intent.getDoubleExtra("startLon" ,0);
                double endLat = intent.getDoubleExtra("endLat" ,0);
                double endLon = intent.getDoubleExtra("endLon" ,0);
                int tripYear = intent.getIntExtra("tripYear" ,0);
                int tripMonth = intent.getIntExtra("tripMonth" ,0);
                int tripDay = intent.getIntExtra("tripDay" ,0);
                int tripHour = intent.getIntExtra("tripHour" ,0);
                int tripMin = intent.getIntExtra("tripMin" ,0);

                Trip trip = new Trip(tripName ,startLat ,startLon ,endLat ,endLon ,tripYear ,tripMonth ,tripDay ,tripHour ,tripMin);

                NavigationFragment fragment = new NavigationFragment(trip);
                transaction.replace(R.id.main_fragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
}