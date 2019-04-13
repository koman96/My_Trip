package com.example.mytrip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class PastAdapter extends BaseAdapter {

    private ArrayList<Trip> trips;
    private FragmentActivity activity;
    private Boolean clicked;

    public PastAdapter (ArrayList<Trip> trips ,FragmentActivity activity){
        this.trips = trips;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.past_row ,null);

        TextView trip_Name = view.findViewById(R.id.trip_Name);
        TextView trip_Date = view.findViewById(R.id.trip_Date);
        TextView trip_Time = view.findViewById(R.id.trip_Time);
        Button delete = view.findViewById(R.id.delete);

        final Trip trip = trips.get(position);

        trip_Name.setText(trip.getTripName() );
        trip_Date.setText(trip.getTrip_year()+"  "+trip.getTrip_month()+"  "+trip.getTrip_day() );
        trip_Time.setText(trip.getTrip_hour() +" : "+trip.getTrip_min() );

        clicked = false;

        view.setOnClickListener( viewClick ->{
            if (! clicked){
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener( deleteEvent -> {
                    //delete this trip from past trips

                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setMessage("are you sure you want delete this trip ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                    dialog.dismiss();

                                    DB db = new DB(activity);
                                    SQLiteDatabase edit = db.getWritableDatabase();

                                    int confirm = edit.delete("pastTrips" ,"trip_name LIKE ?" ,new String[] {trip.getTripName()} );

                                    if (confirm == 1){
                                        //deleted
                                        Toast.makeText(activity ,"trip is deleted from past trips" ,Toast.LENGTH_LONG).show();
                                        trips.remove(position);
                                        notifyDataSetChanged();
                                    }
                                    else
                                        Toast.makeText(activity ,"couldn't delete the trip" ,Toast.LENGTH_LONG).show();

                                    db.close();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                });

                clicked = true;
            }
            else {
                delete.setVisibility(View.GONE);
                clicked = false;
            }
        });


        return view;
    }
}