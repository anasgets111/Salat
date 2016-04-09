package com.gmail.anasgets.salat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CONTACTS = 1;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION};
    //a Calender Object for my calculations
    Calendar cal = Calendar.getInstance();
    // new Prayer Object which is responsible for prayer calculations
    PrayTime prayers = new PrayTime();
    // initiate holders for the text views to change
    TextView day = null;

    //Switch local = null;
    TextView fajr = null;
    TextView shoroq = null;
    TextView dohr = null;
    TextView asr = null;
    TextView magrb = null;
    TextView isha = null;
    //hopefully will be able to pull the location from the device with LocationManager
    double latitude = 1;
    double longitude = 1;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private View mLayout;

    public boolean getLocation() {


        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.

            requestPermission();
            return true;
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            showLocation();
            return false;
        }
    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.


            // Display a SnackBar with an explanation and a button to trigger the request.


            Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_CONTACT,
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    private void showLocation() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 60 * 1000, 1000, locationListener);

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }











    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.sample_main_layout);


        //the method responsible for changing LonLat values
        getLocation();

        //linking the pre-initiated TextViews objects with the ones from the layout
        day = (TextView) findViewById(R.id.day);
        fajr = (TextView) findViewById(R.id.fagrv);
        shoroq = (TextView) findViewById(R.id.shroqv);
        dohr = (TextView) findViewById(R.id.dohrv);
        asr = (TextView) findViewById(R.id.asrv);
        magrb = (TextView) findViewById(R.id.maghrbv);
        isha = (TextView) findViewById(R.id.ishav);


/*
        local = (Switch) findViewById(R.id.switch1);
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), MainActivity.class);
                startActivity(i);
                local.getTextOn();
            }
        });

*/


// new GeoCoder object to get the country, city of the location pulled from device
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null; // Here 1 represent max location result to returned, by documents it recommended 1 to 5;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // If any additional address line present than only,
        // check with max available address lines by getMaxAddressLineIndex()

        String city = addresses.get(0).getAddressLine(2);
        String state = addresses.get(0).getAddressLine(3);
        String country = addresses.get(0).getCountryName();

// geting the timezone , as its the 3rd var needed
        double timezone = prayers.getBaseTimeZone();

        //a switch case to print the day name
        switch (Calendar.DAY_OF_WEEK) {
            case 0:
                day.setText(R.string.Sunday);
                break;
            case 1:
                day.setText(R.string.Monday);
                break;
            case 2:
                day.setText(R.string.Tuesday);
                break;
            case 3:
                day.setText(R.string.Wednesday);
                break;
            case 4:
                day.setText(R.string.Thursday);
                break;
            case 5:
                day.setText(R.string.Friday);
                break;
            case 6:
                day.setText(R.string.Saturday);
                break;
        }

//setting time to 12H per default
        prayers.setTimeFormat(prayers.Time12);

//printint timezone and City in a toast message
        Toast.makeText(this, "Timezone: " + (int) timezone, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "City: " + city + " " + state + " " + country, Toast.LENGTH_LONG).show();

        // all remaining variables to calc the times
        prayers.setCalcMethod(prayers.Egypt); //Egypt Method
        prayers.setAsrJuristic(prayers.Shafii); //Shafii method for Asr
        prayers.setAdjustHighLats(prayers.AngleBased); // AngleBased Method for Altitude
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);
        cal.getTime();  //calculating
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                latitude, longitude, timezone);
//printing each Prayer time to its appropriate TexView
        fajr.setText(prayerTimes.get(0));
        shoroq.setText(prayerTimes.get(1));
        dohr.setText(prayerTimes.get(2));
        asr.setText(prayerTimes.get(3));
        magrb.setText(prayerTimes.get(5));
        isha.setText(prayerTimes.get(6));


    }


}





