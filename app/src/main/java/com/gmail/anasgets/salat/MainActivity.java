package com.gmail.anasgets.salat;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    //a Calender Object for my calculations
    Calendar cal = Calendar.getInstance();
    // new Prayer Object which is responsible for prayer calculations
    PrayTime prayers = new PrayTime();


    // initiate holders for the text views to change
    TextView day = null;
    TextView fajr = null;
    TextView shoroq = null;
    TextView dohr = null;
    TextView asr = null;
    TextView magrb = null;
    TextView isha = null;

    //hopefully will be able to pull the location from the device with LocationManager
    double latitude = 30;
    double longitude = 31;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getLocation(); //the method responsible for changing LonLat values

        //linking the pre-initiated TextViews objects with the ones from the layout
        day = (TextView) findViewById(R.id.day);
        fajr = (TextView) findViewById(R.id.fagrv);
        shoroq = (TextView) findViewById(R.id.shroqv);
        dohr = (TextView) findViewById(R.id.dohrv);
        asr = (TextView) findViewById(R.id.asrv);
        magrb = (TextView) findViewById(R.id.maghrbv);
        isha = (TextView) findViewById(R.id.ishav);

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





