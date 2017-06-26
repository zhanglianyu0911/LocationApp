package com.example.zhang.productapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zhang.productapp.R;
import com.example.zhang.productapp.adapter.LocationAdapter;
import com.example.zhang.productapp.api.ApiClient;
import com.example.zhang.productapp.api.ApiInterface;
import com.example.zhang.productapp.model.LocationModel;
import com.example.zhang.productapp.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrew Zhang on 6/23/2017.
 *
 * First Page, List All Locations, also get current location
 * store in SharePreferences for all app use
 */


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final String LIST_ORDER_STATE = "LIST_ORDER_STATE";
    private final String LIST_POSITION_STATE = "LIST_STATE";
    private List<LocationModel> productList;
    private ApiInterface apiInterface;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;
    private LocationManager manager;
    private LocationManager locationManager;
    private LocationListener listener;
    private Parcelable listState;
    private ArrayList<LocationModel> newArr;

    @BindView(R.id.progress) ProgressBar mProgressBar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.main_toolbar) Toolbar mToolbar;

    private LocationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        productList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        loadData();
        configureGPS();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadData() {

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<LocationModel>> call = apiInterface.getProductData();

        call.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                productList = response.body();
                Collections.sort(productList, new NameComparator());
                mAdapter = new LocationAdapter(productList);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {

            }
        });
        mProgressBar.setVisibility(mProgressBar.GONE);

    }

    public double getDistance(double lat1,double long1, double lat2, double long2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(long1);
        Location locationB = new Location("B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(long2);
        distance = locationA.distanceTo(locationB);

        return distance;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sort_name:
                Collections.sort(productList, new NameComparator());
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_arrive_time:
                Collections.sort(productList, new DateComparator());
                mAdapter.notifyDataSetChanged();
                newArr = (ArrayList<LocationModel>) productList;
                return true;
            case R.id.sort_distance:
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    Toast.makeText(getApplicationContext(),"GPS is not enabled",Toast.LENGTH_LONG).show();
                }else {
                    Collections.sort(productList, new NumberComparator());
                    mAdapter.notifyDataSetChanged();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configureGPS();
                break;
            default:
                break;
        }
    }

    private void configureGPS(){
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean isGPSEnabled = manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isNetworkConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getResources().getString(R.string.current_lat),String.valueOf(location.getLatitude()));
                editor.putString(getResources().getString(R.string.current_lng),String.valueOf(location.getLongitude()));
                editor.commit();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }else {
                if(!isGPSEnabled){
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = recyclerView.getLayoutManager().onSaveInstanceState();

        outState.putParcelable(LIST_POSITION_STATE, mListState);
        outState.putParcelableArrayList(LIST_ORDER_STATE, newArr);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {

        if(state instanceof Bundle){
            Log.e("restore", "save");
            mListState = state.getParcelable(LIST_POSITION_STATE);
        } else {
            Log.e("restore", "not save");
        }
        super.onRestoreInstanceState(state);
    }

// Re-write comparator for sorting
class NameComparator implements Comparator<LocationModel>{

    @Override
    public int compare(LocationModel left, LocationModel right) {
        return left.getLocation().compareTo(right.getLocation());
    }

}

class DateComparator implements Comparator<LocationModel> {

    @Override
    public int compare(LocationModel left, LocationModel right) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(Utils.reFormatTime(left.getArrivalTime()));
                d2 = sdf.parse(Utils.reFormatTime(right.getArrivalTime()));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (d1 != null && d1.after(d2)) {
                return -1;

            } else {
                return 1;
            }
        }
    }

class NumberComparator implements Comparator <LocationModel> {

    @Override
    public int compare(LocationModel left, LocationModel right) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        double distance1 = getDistance(Double.valueOf(prefs.getString(getResources().getString(R.string.current_lat),"")),
                Double.valueOf(prefs.getString(getResources().getString(R.string.current_lng),"")),
                Double.parseDouble(left.getLocLatitude()),
                Double.parseDouble(left.getLocLongitude()));

        double distance2 = getDistance(Double.valueOf(prefs.getString(getResources().getString(R.string.current_lat),"")),
                Double.valueOf(prefs.getString(getResources().getString(R.string.current_lng),"")),
                Double.parseDouble(right.getLocLatitude()),
                Double.parseDouble(right.getLocLongitude()));

        return Double.valueOf(distance1).compareTo(Double.valueOf(distance2));
    }
}

}
