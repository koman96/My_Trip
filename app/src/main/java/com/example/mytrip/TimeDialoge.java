package com.example.mytrip;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

@SuppressLint("ValidFragment")
public class TimeDialoge extends DialogFragment implements View.OnClickListener {

    private TimePicker timePicker;
    private View externalView;
    private FragmentActivity context;
    private int FLAG = 0;   //0 = coming from add     1 = coming from edit
    private Trip trip;

    @SuppressLint("ValidFragment")
    public TimeDialoge(FragmentActivity context ,View view) {
        this.context = context;
        externalView = view;
    }

    public TimeDialoge(FragmentActivity context ,View view ,Trip trip) {
        this.context = context;
        externalView = view;
        this.trip = trip;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_picker ,container ,false);

        Button saveTime = view.findViewById(R.id.saveTime);
        saveTime.setOnClickListener(this);

        timePicker = view.findViewById(R.id.time_picker);

        return view;
    }


    @Override
    public void onClick(View v) {
        //what happens when user click save time
        int hour ,min;

        if (Build.VERSION.SDK_INT >= 23 ){
            hour = timePicker.getHour();
            min = timePicker.getMinute();
        }
        else {  //deprecated
            hour = timePicker.getCurrentHour();
            min = timePicker.getCurrentMinute();
        }


        if (FLAG == 0) {
            AddingFragment fragment = new AddingFragment();
            fragment.show_selected_time(hour, min, externalView, context);
        }
        else {
            EditFragment fragment = new EditFragment();
            fragment.show_selected_time(hour, min, externalView, context ,trip);
        }

        this.dismiss();
    }


    public void changeFLAG(){
        FLAG = 1;
    }
}