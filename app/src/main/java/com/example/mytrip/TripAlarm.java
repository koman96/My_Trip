package com.example.mytrip;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;

public class TripAlarm extends Service {
    private Context context = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getTrip();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startAlarm(Trip trip){
            //play alarm sound
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setMessage("time for trip : " + trip.getTripName())
                    .setPositiveButton("start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                            r.stop();

                            Intent intent = new Intent(context ,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            //put data into intent
                            intent.putExtra("tripName" ,trip.getTripName() );
                            intent.putExtra("startLat" ,trip.getStart_lat() );
                            intent.putExtra("startLon" ,trip.getStart_lon() );
                            intent.putExtra("endLat" ,trip.getEnd_lat() );
                            intent.putExtra("endLon" ,trip.getEnd_lon() );
                            intent.putExtra("tripYear" ,trip.getTrip_year() );
                            intent.putExtra("tripMonth" ,trip.getTrip_month() );
                            intent.putExtra("tripDay" ,trip.getTrip_day() );
                            intent.putExtra("tripHour" ,trip.getTrip_hour() );
                            intent.putExtra("tripMin" ,trip.getTrip_min() );

                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add notification
                            r.stop();

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setContentTitle("Trip Alarm")
                                            .setSmallIcon(R.mipmap.baseline_commute_black_24)
                                            .setContentText("it's time for Trip : " + trip.getTripName())
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setContentIntent(pi);

                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(0, mBuilder.build());
                        }
                    })
                    .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            r.stop();

                            //remove trip from comingTrips and place it in pastTrips
                            DB db = new DB(context);
                            SQLiteDatabase database = db.getWritableDatabase();

                            database.delete("comingTrips", "trip_name LIKE ?", new String[]{trip.getTripName()});
                            long row = database.insert("pastTrips", null, trip.getContentValues());

                            if (row == -1)
                                Toast.makeText(context, "error at adding trip to past trips", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "trip is added to past trips", Toast.LENGTH_LONG).show();

                            db.close();
                        }
                    }).create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            dialog.show();
            r.play();

    }

    private void getTrip(){
        DB db = new DB(context);
        SQLiteDatabase read = db.getReadableDatabase();

        Cursor cursor = read.query("notifiedTrips" ,new String[] {"*"} ,null ,null ,null ,null ,null ,null);
        if (cursor != null){
            Trip trip = null;

            if (cursor.moveToFirst() ){
                trip = new Trip( cursor.getString(cursor.getColumnIndex("trip_name")) ,
                        cursor.getDouble(cursor.getColumnIndex("start_lat")) ,
                        cursor.getDouble(cursor.getColumnIndex("start_lon")) ,
                        cursor.getDouble(cursor.getColumnIndex("end_lat")) ,
                        cursor.getDouble(cursor.getColumnIndex("end_lon")) ,
                        cursor.getInt(cursor.getColumnIndex("trip_year")) ,
                        cursor.getInt(cursor.getColumnIndex("trip_month")) ,
                        cursor.getInt(cursor.getColumnIndex("trip_day")) ,
                        cursor.getInt(cursor.getColumnIndex("trip_hour")) ,
                        cursor.getInt(cursor.getColumnIndex("trip_min")) );
            }
            cursor.close();

            SQLiteDatabase write = db.getWritableDatabase();
            write.delete("notifiedTrips" ,"trip_name LIKE ?" ,new String[] {trip.getTripName()} );

            db.close();

            startAlarm(trip);
        }
    }
}