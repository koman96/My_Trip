package com.example.mytrip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NavigationFragment extends Fragment {

    private FragmentActivity myContext;
    private Trip myTrip;
    private NavigationMapRoute navigationRoute;
    private MapView mapView;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NavigationFragment(Trip trip){
        myTrip = trip;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myContext = getActivity();
        Mapbox.getInstance(myContext ,getResources().getString(R.string.mapBox_token) );

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        define_layout(view ,savedInstanceState);
        return view;
    }


    private void define_layout(View view ,Bundle savedInstanceState){

        mapView = view.findViewById(R.id.mapView);
        Button startNav = view.findViewById(R.id.startNav);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap -> {

            mapboxMap.setStyle(Style.MAPBOX_STREETS);
            LatLng startLatLan = new LatLng( myTrip.getStart_lat() ,myTrip.getStart_lon() );
            LatLng endLatLan = new LatLng( myTrip.getEnd_lat() ,myTrip.getEnd_lon() );

            CameraPosition position = new CameraPosition.Builder()
                    .target(startLatLan)
                    .zoom(8)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position) );


            NavigationRoute.builder(myContext).accessToken(Mapbox.getAccessToken() )
                    .origin(Point.fromLngLat( startLatLan.getLongitude() ,startLatLan.getLatitude() ))
                    .destination(Point.fromLngLat( endLatLan.getLongitude() ,endLatLan.getLatitude() ))
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            //
                            if (response.body() == null) {
                                Log.d("ROUTE", "No routes found, make sure you set the right user and access token.");
                                return;
                            }
                            else if (response.body().routes().size() < 1) {
                                Log.d("ROUTE", "No routes found");
                                return;
                            }

                            //start routing
                            DirectionsRoute route = response.body().routes().get(0);

                            if (navigationRoute != null)
                                navigationRoute.removeRoute();
                            else
                                navigationRoute = new NavigationMapRoute(null ,mapView ,mapboxMap ,R.style.NavigationMapRoute);

                            navigationRoute.addRoute(route);


                            //show navigation button after 3 secs
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //show start navigation button
                                    startNav.setEnabled(true);
                                    startNav.setBackgroundResource(R.color.deepAqua);

                                    startNav.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (route != null){
                                                NavigationLauncherOptions launcherOptions = NavigationLauncherOptions.builder()
                                                        .directionsRoute(route)
                                                        .shouldSimulateRoute(true)
                                                        .build();

                                                NavigationLauncher.startNavigation(myContext ,launcherOptions);
                                            }
                                        }
                                    });
                                }
                            } ,3000);

                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                            Log.d("ROUTE" ,"Error : "+t.getMessage() );
                        }
                    });
        });
    }
}