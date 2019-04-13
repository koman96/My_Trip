package com.example.mytrip;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private FragmentActivity myContext;
    private ArrayList<Trip> myList;
    private boolean isClicked;

    public ListAdapter(FragmentActivity context ,ArrayList<Trip> list){
        myContext = context;
        myList = list;
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        isClicked = false;

        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.row_layout ,null);

        TextView trip_Name = view.findViewById(R.id.trip_Name);
        TextView trip_Date = view.findViewById(R.id.trip_Date);
        TextView trip_Time = view.findViewById(R.id.trip_Time);

        final LinearLayout linearLayout = view.findViewById(R.id.rowActions);
        final Button startTrip = linearLayout.findViewById(R.id.startTrip);
        final Button viewTrip = linearLayout.findViewById(R.id.viewTrip);
        final Button editTrip = linearLayout.findViewById(R.id.editTrip);
        final Button deleteTrip = linearLayout.findViewById(R.id.deleteTrip);

        final Trip trip = myList.get(position);

        trip_Name.setText(trip.getTripName() );
        trip_Date.setText(trip.getTrip_year()+"  "+trip.getTrip_month()+"  "+trip.getTrip_day() );
        trip_Time.setText(trip.getTrip_hour() +" : "+trip.getTrip_min() );

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! isClicked){
                    //first time    ==> show trip actions
                    linearLayout.setVisibility(View.VISIBLE);
                    isClicked = true;

                    startTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //navigation
                            FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.main_fragment ,new NavigationFragment(myList.get(position) ))  //send trip info
                                    .commit();
                        }
                    });

                    viewTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.main_fragment ,new ViewFragment(myList.get(position) ))
                            .commit();
                        }
                    });

                    editTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.main_fragment ,new EditFragment(myList.get(position) ))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    deleteTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog dialog = new AlertDialog.Builder(myContext)
                                    .setMessage("are you sure you want delete trip : "+trip.getTripName() )
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //yes delete trip

                                            //delete from sqlite
                                            DB db = new DB(myContext);
                                            SQLiteDatabase database = db.getWritableDatabase();
                                            database.delete("comingTrips" ,"trip_name LIKE ?" ,new String[]{ trip.getTripName() } );
                                            db.close();

                                            //update list view
                                            myList.remove(position);
                                            notifyDataSetChanged();

                                            dialog.dismiss();
                                            Toast.makeText(myContext ,"trip is deleted" ,Toast.LENGTH_LONG).show();
                                        }

                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //cancel delete
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    });
                }
                else {
                    //clicked before    ==> hide trip action
                    linearLayout.setVisibility(View.GONE);
                    isClicked = false;
                }
            }
        });

        return view;
    }
}