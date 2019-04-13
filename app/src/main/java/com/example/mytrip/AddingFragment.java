package com.example.mytrip;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.geocoder.service.models.GeocoderFeature;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class AddingFragment extends Fragment{

    private TextView tripDate ,tripTime;
    private FragmentActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adding, container, false);
        mContext = getActivity();

        define_attributes(view);
        return view;
    }


    public void define_attributes(final View view){
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        final EditText tripName = view.findViewById(R.id.trip_name);
        final AutoCompleteTextView startingPoint = view.findViewById(R.id.starting_point);
            startingPoint.setDropDownWidth(screenWidth -20);
        final AutoCompleteTextView endingPoint = view.findViewById(R.id.ending_point);
            endingPoint.setDropDownWidth(screenWidth -20);

        tripDate = view.findViewById(R.id.trip_date);
        tripTime = view.findViewById(R.id.trip_time);
        Button save = view.findViewById(R.id.save);

        ImageView menu_icon = view.findViewById(R.id.menu_icon);
        TextView toolbar_header = view.findViewById(R.id.toolbar_header);
            toolbar_header.setText("Add Trip");
        final DrawerLayout drawer = view.findViewById(R.id.drawer);

        Button main_btn = view.findViewById(R.id.main_btn);
        Button pastTrips_btn = view.findViewById(R.id.pastTrips_btn);
        Button sync_btn = view.findViewById(R.id.sync_btn);
        Button signOut_btn = view.findViewById(R.id.signOut_btn);
        Button deleteAcc_btn = view.findViewById(R.id.deleteAcc_btn);


        final PlaceAdapter adapter = new PlaceAdapter(mContext);
        startingPoint.setAdapter(adapter);
        endingPoint.setAdapter(adapter);

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

        startingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                GeocoderFeature result = adapter.getItem(position);

                editor.putFloat("tripStartLat" , (float) result.getLatitude() )
                .putFloat("tripStartLon" , (float) result.getLongitude() ).commit();

                startingPoint.setText(null);
                startingPoint.setHint("confirmed");

                startingPoint.clearComposingText();
            }
        });

        endingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                GeocoderFeature result = adapter.getItem(position);
                editor.putFloat("tripEndLat" , (float) result.getLatitude() )
                        .putFloat("tripEndLon" , (float) result.getLongitude() ).commit();

                endingPoint.setText(null);
                endingPoint.setHint("confirmed");

                endingPoint.clearAnimation();
            }
        });

        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                DateDialoge dateDialoge = new DateDialoge(mContext ,view);
                dateDialoge.show(transaction ,null);
            }
        });

        tripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                TimeDialoge timeDialoge = new TimeDialoge(mContext ,view);
                timeDialoge.show(transaction ,null);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trip_name = tripName.getText().toString();
                trip_name.trim();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);

                if (trip_name.isEmpty() )
                    Toast.makeText(getActivity() ,"please enter trip name" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripStartLat") )
                    Toast.makeText(getActivity() ,"please select starting point of trip" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripEndLat") )
                    Toast.makeText(getActivity() ,"please select ending point of trip" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripYear") )
                    Toast.makeText(getActivity() ,"please select trip date" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripHour") )
                    Toast.makeText(getActivity() ,"please select trip time" ,Toast.LENGTH_SHORT).show();

                else {
                    //add trip to sqlite
                    double tripStartLat =  sharedPreferences.getFloat("tripStartLat" ,0);
                    double tripStartLon =  sharedPreferences.getFloat("tripStartLon" ,0);
                    double tripEndtLat =  sharedPreferences.getFloat("tripEndLat" ,0);
                    double tripEndLon =  sharedPreferences.getFloat("tripEndLon" ,0);

                    int tripYear = sharedPreferences.getInt("tripYear" ,0);
                    int tripMonth = sharedPreferences.getInt("tripMonth" ,0);
                    int tripDay = sharedPreferences.getInt("tripDay" ,0);

                    int tripHour = sharedPreferences.getInt("tripHour" ,0);
                    int tripMin = sharedPreferences.getInt("tripMin" ,0);

                    //add to sqlite database
                    Trip myTrip = new Trip(trip_name ,tripStartLat ,tripStartLon ,tripEndtLat ,tripEndLon ,tripYear ,tripMonth ,tripDay ,tripHour ,tripMin);
                    addTo_dataBase(myTrip);
                }
            }
        });

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_mainPage();
            }
        });

        pastTrips_btn.setOnClickListener( e->{
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment ,new PastFragment() )
                    .addToBackStack(null)
                    .commit();
        });

        sync_btn.setOnClickListener( syncEvent ->{
            Boolean hasData = false;

            //remove old data from server
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            ref.child("coming Trips").child(userId).removeValue();
            ref.child("past Trips").child(userId).removeValue();
            ref.child("notified Trips").child(userId).removeValue();

            //get data from sqlite
            DB db = new DB( mContext );
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
                Toast.makeText(mContext ,"your data has been syncced" ,Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext ,"you don't have any data" ,Toast.LENGTH_LONG).show();
        });

        signOut_btn.setOnClickListener( e->{

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(mContext ,MainActivity.class) );
        });

        deleteAcc_btn.setOnClickListener(e->{
            AlertDialog dialog = new AlertDialog.Builder(getActivity() )
                    .setTitle("Delete Account")
                    .setMessage("delete all your data : coming trips ,past trips ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                            DB db = new DB(mContext);
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

                            startActivity(new Intent(mContext ,MainActivity.class) );
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    })
                    .show();
        });
    }


    public void show_selected_date(int year ,int month ,int day ,View view ,FragmentActivity context){
        if (tripDate == null)
            tripDate = view.findViewById(R.id.trip_date);

        mContext = context;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tripYear" ,year).putInt("tripMonth" ,month).putInt("tripDay" ,day).commit();
        tripDate.setText(year+"  "+month+"  "+day);
    }

    public void show_selected_time(int hour ,int min ,View view ,FragmentActivity context) {
        if (tripTime == null)
            tripTime = view.findViewById(R.id.trip_time);

        mContext = context;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tripHour" ,hour).putInt("tripMin" ,min).commit();
        tripTime.setText(hour +" : "+ min);
    }

    private void show_mainPage(){
        FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment ,new MainFragment() )
                .commit();
    }

    private void addTo_dataBase(Trip trip){
        DB database = new DB(mContext);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

        long row = sqLiteDatabase.insert("comingTrips", null, trip.getContentValues() );

        if (row == -1)
            Toast.makeText(mContext, "error at adding trip to database", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(mContext, "trip is added successfully", Toast.LENGTH_SHORT).show();
            mContext.getSharedPreferences("Pref" ,Context.MODE_PRIVATE).edit().clear().commit();
        }

        //update list of notified trips
        sqLiteDatabase.insert("notifiedTrips" ,null ,trip.getContentValues() );

        set_alarm(trip);
    }

    private void set_alarm(Trip myTrip){
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext ,TripAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getService(mContext ,0 ,intent ,0);

        Calendar calendar = new GregorianCalendar(TimeZone.getDefault() );
        calendar.set(Calendar.YEAR ,myTrip.getTrip_year() );
        calendar.set(Calendar.MONTH ,myTrip.getTrip_month()-1 );
        calendar.set(Calendar.DAY_OF_MONTH ,myTrip.getTrip_day() );
        calendar.set(Calendar.HOUR_OF_DAY ,myTrip.getTrip_hour() );
        calendar.set(Calendar.MINUTE ,myTrip.getTrip_min() );
        calendar.set(Calendar.SECOND ,00);
        calendar.set(Calendar.MILLISECOND ,00);

        manager.set(AlarmManager.RTC_WAKEUP ,calendar.getTimeInMillis() ,pendingIntent);

        show_mainPage();
    }
}