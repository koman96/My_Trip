package com.example.mytrip;

public class FirebaseTrip {

    private String tripName;
    private double start_lat;
    private double start_lon;
    private double end_lat;
    private double end_lon;
    private int trip_year;
    private int trip_month;
    private int trip_day;
    private int trip_hour;
    private int trip_min;

    public FirebaseTrip(String tripName ,double start_lat ,double start_lon ,double end_lat ,double end_lon ,
                int trip_year ,int trip_month ,int trip_day ,int trip_hour ,int trip_min){
        this.tripName = tripName;
        this.start_lat = start_lat;
        this.start_lon = start_lon;
        this.end_lat = end_lat;
        this.end_lon = end_lon;

        this.trip_year = trip_year;
        this.trip_month = trip_month;
        this.trip_day = trip_day;

        this.trip_hour = trip_hour;
        this.trip_min = trip_min;
    }

    public String getTripName() {
        return tripName;
    }

    public int getTrip_year() {
        return trip_year;
    }

    public int getTrip_month() {
        return trip_month;
    }

    public int getTrip_day() {
        return trip_day;
    }

    public int getTrip_hour() {
        return trip_hour;
    }

    public int getTrip_min() {
        return trip_min;
    }

    public double getStart_lat() {
        return start_lat;
    }

    public double getStart_lon() {
        return start_lon;
    }

    public double getEnd_lat() {
        return end_lat;
    }

    public double getEnd_lon() {
        return end_lon;
    }

}