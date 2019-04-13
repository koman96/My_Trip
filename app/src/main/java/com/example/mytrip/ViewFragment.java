package com.example.mytrip;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class ViewFragment extends Fragment {

    private Trip myTrip;
    private FragmentActivity myContext;
    private MapView mapView;

    public ViewFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ViewFragment(Trip trip) {
        myTrip = trip;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myContext = getActivity();
        Mapbox.getInstance(myContext ,getResources().getString(R.string.mapBox_token ));

        View view = inflater.inflate(R.layout.fragment_view, container, false);
        define_layout(view ,savedInstanceState);

        return view;
    }


    private void define_layout(View view ,Bundle savedInstanceState) {
        TextView trip_name = view.findViewById(R.id.trip_name);
        TextView starting_point = view.findViewById(R.id.starting_point);
        TextView ending_point = view.findViewById(R.id.ending_point);
        TextView trip_date = view.findViewById(R.id.trip_date);
        TextView trip_time = view.findViewById(R.id.trip_time);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        trip_name.setText(myTrip.getTripName() );
        trip_date.setText(myTrip.getTrip_year()+"  "+myTrip.getTrip_month()+"  "+myTrip.getTrip_day() );
        trip_time.setText(myTrip.getTrip_hour()+" : "+myTrip.getTrip_min() );


        Geocoder geocoder = new Geocoder(myContext , Locale.getDefault() );

        try {

            ArrayList<Address> startAdd = (ArrayList<Address>) geocoder.getFromLocation( myTrip.getStart_lat(), myTrip.getStart_lon(), 1);
            starting_point.setText( startAdd.get(0).getAddressLine(0) );

            ArrayList<Address> endAdd = (ArrayList<Address>) geocoder.getFromLocation( myTrip.getEnd_lat(), myTrip.getEnd_lon(), 1);
            ending_point.setText( endAdd.get(0).getAddressLine(0) );
        }
        catch (IOException e){
            Toast.makeText(myContext ,"can't get your trip addresses" ,Toast.LENGTH_LONG).show();
        }


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS);

                LatLng startLatLan = new LatLng( myTrip.getStart_lat() ,myTrip.getStart_lon() );
                LatLng endLatLan = new LatLng( myTrip.getEnd_lat() ,myTrip.getEnd_lon() );

                CameraPosition position = new CameraPosition.Builder()
                        .target(startLatLan)
                        .zoom(6)
                        .build();

                mapboxMap.addMarker( new MarkerOptions()
                        .title("start")
                        .setPosition(startLatLan) );

                mapboxMap.addMarker( new MarkerOptions()
                        .title("end")
                        .setPosition(endLatLan) );

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position) );
            }
        });
    }
}