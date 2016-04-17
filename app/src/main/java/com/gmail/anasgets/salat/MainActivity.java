package com.gmail.anasgets.salat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {


    //Switch object for UI Language
    Switch langS; //= null;
    //a Calender Object for my calculations
    Calendar cal = Calendar.getInstance();
    // new Prayer Object which is responsible for prayer calculations
    PrayTime prayers = new PrayTime();

    // geting the timezone , as its the 3rd var needed
    double timezone = prayers.getBaseTimeZone();

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
    double latitude = 30;
    double longitude = 30;

    public void toast(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();


    }

    /*
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            newConfig.setLocale(Locale.forLanguageTag("ar"));
            super.onConfigurationChanged(newConfig);
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = getSharedPreferences("com.gmail.anasgets.salat", MODE_PRIVATE);
        langS = (Switch) findViewById(R.id.switch1);


        langS.setChecked(sharedPrefs.getBoolean("LangStatus", false));

        //attach a listener to check for changes in state
        langS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.gmail.anasgets.salat", MODE_PRIVATE).edit();
                    editor.putBoolean("LangStatus", true);
                    editor.apply();
                    setLocale("ar");
                    getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.gmail.anasgets.salat", MODE_PRIVATE).edit();
                    editor.putBoolean("LangStatus", false);
                    editor.apply();
                    setLocale("en");
                }

            }
        });


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


    }


    @Override
    protected void onResume() {
        super.onResume();


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
        if (addresses != null) {
            String city = addresses.get(0).getAddressLine(2);
            String state = addresses.get(0).getAddressLine(3);
            String country = addresses.get(0).getCountryName();
            toast(this.getString(R.string.city) + " " + city + " " + state + " " + country);
        } else {
            toast(this.getString(R.string.empty));
        }

        //setting time to 12H per default
        prayers.setTimeFormat(prayers.Time12);

//printint timezone and City in a toast message
        if (timezone > 0) {
            toast(getString(R.string.timezone) + " +" + (int) timezone);
        } else {
            toast(getString(R.string.timezone) + " " + (int) timezone);
        }

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