package com.example.zhang.productapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhang on 6/23/2017.
 */

public class Utils {

    public static final String BASE_API = "https://maps.googleapis.com/maps/api/directions/json?";

    public static String reFormatTime(String time) {
        String[] splitTeimt = time.split("T");
        String newTime = splitTeimt[0] + " " + splitTeimt[1];

        return newTime;
    }

}
