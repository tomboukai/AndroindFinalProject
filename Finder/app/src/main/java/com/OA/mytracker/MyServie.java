package com.OA.mytracker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MyServie extends Service {
    public static boolean IsRunning = false;
    DatabaseReference databaseReference;
    public static Location Location;
    FusedLocationProviderClient fusedLocationProviderClient;
    private UserInformation uInfo = new UserInformation();
    private String numbeT;
    //private Location Location1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IsRunning = true;
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GlobalInfo globalInfo = new GlobalInfo(this);
        globalInfo.LoadData();

            //first_method
           fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
          fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location location)
              {
                if (location!=null)
                {
                    upload(location);
                }
                else
                {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 24.9786775,55.1684037);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
              }


          });
          //

        return START_NOT_STICKY;
    }
    public  void upload(final Location location)
    {




        for (Map.Entry m : GlobalInfo.MyTrackers.entrySet()) {
            numbeT = m.getKey().toString();
            System.out.println("OMER"+numbeT);
        }
        if(numbeT==null) return;

        System.out.println("NUMBER "+numbeT);
        databaseReference.child("Users").child(numbeT).
                child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    System.out.println("KEY :"+ds.getKey());
                    switch (ds.getKey()) {
                        case "LastOnlineDate":
                            uInfo.setLastOnlineDate(ds.getValue().toString());
                            break;
                        case "lat":
                            uInfo.setLat((Double) ds.getValue());
                            break;
                        case "lag":
                            uInfo.setLon((Double) ds.getValue());
                            break;
                        default:
                            // code block
                    }

                }
                if(uInfo.getLon()==null) return;
                Double result = FlatEarthDist.distance(location.getLatitude(), uInfo.getLat(), location.getLongitude(), uInfo.getLon());
                if (result > 5) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Users").child(GlobalInfo.PhoneNumber).
                child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (location == null) return;
                System.out.println("Location lat:" + location.getLongitude());
                System.out.println("Location log:" + location.getLongitude());
                databaseReference.child("Users").
                        child(GlobalInfo.PhoneNumber).child("Location").child("lat")
                        .setValue(location.getLatitude());

                databaseReference.child("Users").
                        child(GlobalInfo.PhoneNumber).child("Location").child("lag")
                        .setValue(location.getLongitude());

                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date = new Date();
                databaseReference.child("Users").
                        child(GlobalInfo.PhoneNumber).child("Location").
                        child("LastOnlineDate")
                        .setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
