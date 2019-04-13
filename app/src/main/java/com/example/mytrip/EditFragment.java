package com.example.mytrip;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.geocoder.service.models.GeocoderFeature;


@SuppressLint("ValidFragment")
public class EditFragment extends Fragment {

    private FragmentActivity mContext;
    private TextView tripDate ,tripTime;
    private Trip oldTrip;


    @SuppressLint("ValidFragment")
    public EditFragment(Trip trip) {
        oldTrip = trip;
    }

    public EditFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit, container, false);
        mContext = getActivity();

        define_layout(view);
        return view;
    }

    private void define_layout(final View view){
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        final EditText tripName = view.findViewById(R.id.trip_name);
        final AutoCompleteTextView startingPoint = view.findViewById(R.id.starting_point);
            startingPoint.setDropDownWidth(screenWidth-20);
        final AutoCompleteTextView endingPoint = view.findViewById(R.id.ending_point);
            endingPoint.setDropDownWidth(screenWidth-20);

        tripDate = view.findViewById(R.id.trip_date);
        tripTime = view.findViewById(R.id.trip_time);

        Button edit = view.findViewById(R.id.edit);

        final PlaceAdapter adapter = new PlaceAdapter(mContext);
        startingPoint.setAdapter(adapter);
        endingPoint.setAdapter(adapter);

        //printOldTrip
        tripName.setText(oldTrip.getTripName() );
        tripDate.setText(oldTrip.getTrip_year()+"  "+oldTrip.getTrip_month()+"  "+oldTrip.getTrip_day() );
        tripTime.setText(oldTrip.getTrip_hour()+" : "+oldTrip.getTrip_min() );

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tripYear" ,oldTrip.getTrip_year()).putInt("tripMonth" ,oldTrip.getTrip_month()).putInt("tripDay" ,oldTrip.getTrip_day()).commit();
        editor.putInt("tripHour" ,oldTrip.getTrip_hour()).putInt("tripMin" ,oldTrip.getTrip_min()).commit();


        //events
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
                //startingPoint
            }
        });

        endingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                GeocoderFeature result = adapter.getItem(position);
                editor.putFloat("tripEndtLat" , (float) result.getLatitude() )
                        .putFloat("tripEndtLon" , (float) result.getLongitude() ).commit();

                endingPoint.setText(null);
                endingPoint.setHint("confirmed");
                //close
            }
        });

        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                DateDialoge dateDialoge = new DateDialoge(mContext ,view);
                dateDialoge.changeFLAG();   //let it know this is edit process

                dateDialoge.show(transaction ,null);
            }
        });

        tripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                TimeDialoge timeDialoge = new TimeDialoge(mContext ,view);
                timeDialoge.changeFLAG();   //let it know this is edit process

                timeDialoge.show(transaction ,null);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trip_name = tripName.getText().toString();
                trip_name.trim();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);

                if (trip_name.isEmpty() )
                    Toast.makeText(getActivity() ,"please enter trip name" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripStartLat") )
                    Toast.makeText(getActivity() ,"please select starting point of trip" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripEndtLat") )
                    Toast.makeText(getActivity() ,"please select ending point of trip" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripYear") )
                    Toast.makeText(getActivity() ,"please select trip date" ,Toast.LENGTH_SHORT).show();

                else if (! sharedPreferences.contains("tripHour") )
                    Toast.makeText(getActivity() ,"please select trip time" ,Toast.LENGTH_SHORT).show();

                else {
                    //edit trip to sqlite
                    double tripStartLat = sharedPreferences.getFloat("tripStartLat", 0);
                    double tripStartLon = sharedPreferences.getFloat("tripStartLon", 0);
                    double tripEndtLat = sharedPreferences.getFloat("tripEndtLat", 0);
                    double tripEndLon = sharedPreferences.getFloat("tripEndLon", 0);

                    int tripYear = sharedPreferences.getInt("tripYear", 0);
                    int tripMonth = sharedPreferences.getInt("tripMonth", 0);
                    int tripDay = sharedPreferences.getInt("tripDay", 0);

                    int tripHour = sharedPreferences.getInt("tripHour", 0);
                    int tripMin = sharedPreferences.getInt("tripMin", 0);

                    ContentValues values = new ContentValues();
                    values.put("trip_name" ,trip_name);
                    values.put("start_lat" ,tripStartLat);
                    values.put("start_lon" ,tripStartLon);
                    values.put("end_lat" ,tripEndtLat);
                    values.put("end_lon" ,tripEndLon);
                    values.put("trip_year" ,tripYear);
                    values.put("trip_month" ,tripMonth);
                    values.put("trip_day" ,tripDay);
                    values.put("trip_hour" ,tripHour);
                    values.put("trip_min" ,tripMin);

                    //modify database
                    DB db = new DB(mContext);
                    SQLiteDatabase database = db.getWritableDatabase();
                    database.update("comingTrips" ,values ,"trip_name LIKE ?" ,new String[] {oldTrip.getTripName()} );
                    db.close();

                    Toast.makeText(mContext ,"trip is edited successfully" ,Toast.LENGTH_LONG).show();
                    sharedPreferences.edit().clear();

                    //show main page
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment ,new MainFragment() )
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }


    public void show_selected_date(int year ,int month ,int day ,View view ,FragmentActivity context ,Trip trip){
        if (tripDate == null)
            tripDate = view.findViewById(R.id.trip_date);

        mContext = context;
        oldTrip = trip;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tripYear" ,year).putInt("tripMonth" ,month).putInt("tripDay" ,day).commit();
        tripDate.setText(year+"  "+month+"  "+day);
    }


    public void show_selected_time(int hour ,int min ,View view ,FragmentActivity context ,Trip trip) {
        if (tripTime == null)
            tripTime = view.findViewById(R.id.trip_time);

        mContext = context;
        oldTrip = trip;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Pref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tripHour" ,hour).putInt("tripMin" ,min).commit();
        tripTime.setText(hour +" : "+ min);
    }
}