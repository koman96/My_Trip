package com.example.mytrip;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.mapbox.geocoder.GeocoderCriteria;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.geocoder.service.models.GeocoderResponse;
import java.io.IOException;
import java.util.List;
import retrofit.Response;

public class PlaceAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private GeocoderFilter geocoderFilter;
    private List<GeocoderFeature> features;
    private final String API_token = "pk.eyJ1IjoibW9qbzIyMiIsImEiOiJjanNpeGN2b2ExdThpNDNzNzRmeTFscng0In0.1t1yBAbsqh8-X-iX-H7yLg";


    public PlaceAdapter(Context context){
        this.context = context;
    }


    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public GeocoderFeature getItem(int position) {
        return features.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        else
            view = convertView;

        TextView text = (TextView) view;

        GeocoderFeature feature = getItem(position);
        text.setText(feature.getPlaceName() );

        return view;
    }

    @Override
    public Filter getFilter() {
        if (geocoderFilter == null)
            geocoderFilter = new GeocoderFilter();

        return geocoderFilter;
    }


    //my filter
    private class GeocoderFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint) )
                return results;

            MapboxGeocoder client = new MapboxGeocoder.Builder()
                    .setAccessToken(API_token)
                    .setLocation(constraint.toString() )
                    .setType(GeocoderCriteria.TYPE_POI)
                    .build();


            Response<GeocoderResponse> response;
            try {
                response = client.execute();

            } catch (IOException e) {
                e.printStackTrace();
                return results;
            }

            features = response.body().getFeatures();
            results.values = features;
            results.count = features.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results != null && results.count > 0) {
                features = (List<GeocoderFeature>) results.values;
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }
    }
}