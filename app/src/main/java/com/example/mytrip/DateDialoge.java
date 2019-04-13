package com.example.mytrip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DateDialoge extends DialogFragment implements View.OnClickListener {

    private DatePicker datePicker;
    private View externalView;
    private FragmentActivity context;
    private int FLAG = 0;   //0 = coming from add     1 = coming from edit
    private Trip trip;


    @SuppressLint("ValidFragment")
    public DateDialoge(FragmentActivity context ,View view){
        this.context = context;
        externalView = view;
    }

    public DateDialoge(FragmentActivity context ,View view ,Trip trip){
        this.context = context;
        externalView = view;
        this.trip = trip;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_picker ,container ,false);

        datePicker = view.findViewById(R.id.date_picker);
        Button saveDate = view.findViewById(R.id.saveDate);

        saveDate.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        //what happens when user click save date
        int year = datePicker.getYear();
        int month = datePicker.getMonth()+1;    //0 to 11
        int day = datePicker.getDayOfMonth();


        if (FLAG == 0) {
            AddingFragment fragment = new AddingFragment();
            fragment.show_selected_date(year, month, day, externalView, context);
        }
        else {
            EditFragment fragment = new EditFragment();
            fragment.show_selected_date(year, month, day, externalView, context ,trip);
        }

        this.dismiss();
    }


    public void changeFLAG(){
        FLAG = 1;
    }
}