package com.example.mytrip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class PastFragment extends Fragment {
    private FragmentActivity activity;
    private TextView tripsGuide_txt;
    private ListView trip_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        define_layout(view);

        return view;
    }

    private void define_layout(View view) {
        ImageView menu_icon = view.findViewById(R.id.menu_icon);
        TextView toolbar_header = view.findViewById(R.id.toolbar_header);
        toolbar_header.setText("Past Trips");
        final DrawerLayout drawer = view.findViewById(R.id.drawer);

        tripsGuide_txt = view.findViewById(R.id.tripsGuide_txt);
        trip_list = view.findViewById(R.id.trip_list);

        Button addTrip_btn = view.findViewById(R.id.addTrip_btn);
        Button main_btn = view.findViewById(R.id.main_btn);
        Button sync_btn = view.findViewById(R.id.sync_btn);
        Button signOut_btn = view.findViewById(R.id.signOut_btn);
        Button deleteAcc_btn = view.findViewById(R.id.deleteAcc_btn);

        //events
        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.START) )
                    drawer.closeDrawer(Gravity.START);
                else
                    drawer.openDrawer(Gravity.START);
            }
        });

        addTrip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment ,new AddingFragment() )
                        .addToBackStack(null)
                        .commit();
            }
        });

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment ,new MainFragment() )
                        .addToBackStack(null)
                        .commit();
            }
        });

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean hasData = false;

                //remove old data from server
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                ref.child("coming Trips").child(userId).removeValue();
                ref.child("past Trips").child(userId).removeValue();
                ref.child("notified Trips").child(userId).removeValue();

                //get data from sqlite
                DB db = new DB( activity );
                SQLiteDatabase read = db.getReadableDatabase();

                Cursor comingCursor = read.rawQuery("SELECT * FROM comingTrips" ,null);
                if (comingCursor.moveToFirst() ){
                    while (! comingCursor.isAfterLast() ) {
                        String trip_name = comingCursor.getString(comingCursor.getColumnIndex("trip_name"));
                        double start_lat = comingCursor.getDouble(comingCursor.getColumnIndex("start_lat"));
                        double start_lon = comingCursor.getDouble(comingCursor.getColumnIndex("start_lon"));
                        double end_lat = comingCursor.getDouble(comingCursor.getColumnIndex("end_lat"));
                        double end_lon = comingCursor.getDouble(comingCursor.getColumnIndex("end_lon"));

                        int trip_year = comingCursor.getInt(comingCursor.getColumnIndex("trip_year"));
                        int trip_month = comingCursor.getInt(comingCursor.getColumnIndex("trip_month"));
                        int trip_day = comingCursor.getInt(comingCursor.getColumnIndex("trip_day"));

                        int trip_hour = comingCursor.getInt(comingCursor.getColumnIndex("trip_hour"));
                        int trip_min = comingCursor.getInt(comingCursor.getColumnIndex("trip_min"));

                        ref.child("coming Trips").child(userId).push().setValue(
                                new FirebaseTrip(trip_name, start_lat, start_lon, end_lat, end_lon, trip_year, trip_month, trip_day, trip_hour, trip_min) );

                        comingCursor.moveToNext();
                        hasData = true;
                    }
                    comingCursor.close();
                }

                Cursor pastCursor = read.rawQuery("SELECT * FROM pastTrips" ,null);
                if (pastCursor.moveToFirst() ){
                    while (! pastCursor.isAfterLast() ){
                        String trip_name = pastCursor.getString(pastCursor.getColumnIndex("trip_name"));
                        double start_lat = pastCursor.getDouble(pastCursor.getColumnIndex("start_lat"));
                        double start_lon = pastCursor.getDouble(pastCursor.getColumnIndex("start_lon"));
                        double end_lat = pastCursor.getDouble(pastCursor.getColumnIndex("end_lat"));
                        double end_lon = pastCursor.getDouble(pastCursor.getColumnIndex("end_lon"));

                        int trip_year = pastCursor.getInt(pastCursor.getColumnIndex("trip_year"));
                        int trip_month = pastCursor.getInt(pastCursor.getColumnIndex("trip_month"));
                        int trip_day = pastCursor.getInt(pastCursor.getColumnIndex("trip_day"));

                        int trip_hour = pastCursor.getInt(pastCursor.getColumnIndex("trip_hour"));
                        int trip_min = pastCursor.getInt(pastCursor.getColumnIndex("trip_min"));

                        ref.child("past Trips").child(userId).push().setValue(
                                new FirebaseTrip(trip_name, start_lat, start_lon, end_lat, end_lon, trip_year, trip_month, trip_day, trip_hour, trip_min) );

                        pastCursor.moveToNext();
                        hasData = true;
                    }
                    pastCursor.close();
                }

                Cursor notifiedCursor = read.rawQuery("SELECT * FROM notifiedTrips" ,null);
                if (notifiedCursor.moveToFirst() ){
                    while (! notifiedCursor.isAfterLast() ){
                        String trip_name = notifiedCursor.getString(notifiedCursor.getColumnIndex("trip_name"));
                        double start_lat = notifiedCursor.getDouble(notifiedCursor.getColumnIndex("start_lat"));
                        double start_lon = notifiedCursor.getDouble(notifiedCursor.getColumnIndex("start_lon"));
                        double end_lat = notifiedCursor.getDouble(notifiedCursor.getColumnIndex("end_lat"));
                        double end_lon = notifiedCursor.getDouble(notifiedCursor.getColumnIndex("end_lon"));

                        int trip_year = notifiedCursor.getInt(notifiedCursor.getColumnIndex("trip_year"));
                        int trip_month = notifiedCursor.getInt(notifiedCursor.getColumnIndex("trip_month"));
                        int trip_day = notifiedCursor.getInt(notifiedCursor.getColumnIndex("trip_day"));

                        int trip_hour = notifiedCursor.getInt(notifiedCursor.getColumnIndex("trip_hour"));
                        int trip_min = notifiedCursor.getInt(notifiedCursor.getColumnIndex("trip_min"));

                        ref.child("notified Trips").child(userId).push().setValue(
                                new FirebaseTrip(trip_name, start_lat, start_lon, end_lat, end_lon, trip_year, trip_month, trip_day, trip_hour, trip_min) );

                        notifiedCursor.moveToNext();
                        hasData = true;
                    }
                    notifiedCursor.close();
                }
                db.close();

                if (hasData)
                    Toast.makeText(activity,"your data has been syncced" ,Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(activity ,"you don't have any data" ,Toast.LENGTH_LONG).show();
            }
        });

        signOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity() ,MainActivity.class) );
            }
        });

        deleteAcc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity() )
                        .setTitle("Delete Account")
                        .setMessage("delete all your data : coming trips ,past trips ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                DB db = new DB(activity);
                                SQLiteDatabase edit = db.getWritableDatabase();

                                edit.delete("comingTrips" ,null ,null);
                                edit.delete("pastTrips" ,null ,null);
                                edit.delete("notifiedTrips" ,null ,null);

                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                reference.child("coming Trips").child(userID).removeValue();
                                reference.child("past Trips").child(userID).removeValue();
                                reference.child("notified Trips").child(userID).removeValue();

                                FirebaseAuth.getInstance().signOut();
                                dialog.dismiss();

                                startActivity(new Intent(activity ,MainActivity.class) );
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        load_pastTrips();
    }

    private void load_pastTrips(){
        DB db = new DB(activity);
        SQLiteDatabase read = db.getReadableDatabase();

        Cursor cursor = read.rawQuery("SELECT * FROM pastTrips" ,null);

        if (cursor.moveToFirst() ) {
            ArrayList<Trip> trips = new ArrayList<>();

            while (!cursor.isAfterLast() ) {
                String trip_name = cursor.getString(cursor.getColumnIndex("trip_name") );
                double start_lat = cursor.getDouble(cursor.getColumnIndex("start_lat") );
                double start_lon = cursor.getDouble(cursor.getColumnIndex("start_lon") );
                double end_lat = cursor.getDouble(cursor.getColumnIndex("end_lat") );
                double end_lon = cursor.getDouble(cursor.getColumnIndex("end_lon") );

                int trip_year = cursor.getInt(cursor.getColumnIndex("trip_year") );
                int trip_month = cursor.getInt(cursor.getColumnIndex("trip_month") );
                int trip_day = cursor.getInt(cursor.getColumnIndex("trip_day") );

                int trip_hour = cursor.getInt(cursor.getColumnIndex("trip_hour") );
                int trip_min = cursor.getInt(cursor.getColumnIndex("trip_min") );

                trips.add( new Trip(trip_name ,start_lat ,start_lon ,end_lat ,end_lon ,trip_year ,trip_month ,trip_day ,trip_hour ,trip_min) );
                cursor.moveToNext();
            }

            //display it
            PastAdapter adapter = new PastAdapter(trips ,getActivity() );
            trip_list.setAdapter(adapter);
            tripsGuide_txt.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(activity, "there is no trips yet", Toast.LENGTH_LONG).show();
            tripsGuide_txt.setVisibility(View.INVISIBLE);
        }

        db.close();
    }
}