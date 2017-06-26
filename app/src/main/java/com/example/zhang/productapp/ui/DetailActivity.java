package com.example.zhang.productapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.zhang.productapp.R;
import com.example.zhang.productapp.model.DistanceModel;
import com.example.zhang.productapp.model.LocationModel;
import com.example.zhang.productapp.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Andrew Zhang on 6/23/2017.
 */

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String ROUNTINE_KEY = "AIzaSyBOtphUbAsNhuhNC7rtc0bTXS-L_BUALWA";
    private GoogleMap mMap;
    @BindView(R.id.toolbar_detail)
    Toolbar mToolbar;
    @BindView(R.id.arrival_time_value)
    TextView tvArrivetime;
    @BindView(R.id.location_detail)
    TextView tvLocation;
    @BindView(R.id.addrss_map_value)
    TextView tvAddress;
    @BindView(R.id.latitude_map_value)
    TextView tvLat;
    @BindView(R.id.longitude_map_value)
    TextView tvLng;

    private double lat;
    private double lng;
    private LocationModel productData;
    private String originCoordinate;
    private String destinationCoordinate;
    private Map<String,DistanceModel> routineInfo;
    private DistanceModel distanceModel;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        routineInfo = new HashMap();
        showDetails(tvAddress,tvLocation,tvLat,tvLng);
        mToolbar.setTitle(tvLocation.getText().toString());
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.navigate);
        subsribeData(tvArrivetime);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng local = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(local).title(tvLocation.getText().toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(local, 15.0f));
        mMap.isMyLocationEnabled();

    }

    private void showDetails(TextView tvAddress, TextView tvLocation,
                             TextView tvLat, TextView tvLng){
        productData = (LocationModel)getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_key));

        tvAddress.setText(productData.getAddress());
        tvLocation.setText(productData.getLocation());
        tvLat.setText(productData.getLocLatitude());
        tvLng.setText(productData.getLocLongitude());

        lat = Double.valueOf(tvLat.getText().toString());
        lng = Double.valueOf(tvLng.getText().toString());
        destinationCoordinate = lat + "," + lng;
        originCoordinate = prefs.getString(getResources().getString(R.string.current_lat),"") + "," + prefs.getString(getResources().getString(R.string.current_lng),"");

    }

    // load data from google direct API
    @Nullable
    private Map getRoutineInfo() throws IOException, JSONException {

        final String ROUNTINE_API = Utils.BASE_API + "origin=" + originCoordinate
                + "&destination=" + destinationCoordinate
                + "&key=" + ROUNTINE_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ROUNTINE_API)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            JSONObject jsonData = new JSONObject(response.body().string());
            try {
                JSONArray rountineArray = jsonData.getJSONArray("routes");
                for(int i = 0; i<rountineArray.length(); i++) {
                    JSONObject jsonRoutine = rountineArray.getJSONObject(i);
                    JSONArray jsonLegs = jsonRoutine.getJSONArray("legs");
                    JSONObject legObj  = jsonLegs.getJSONObject(0);
                    JSONObject jsonDistance = legObj.getJSONObject("distance");
                    JSONObject jsonDuration = legObj.getJSONObject("duration");
                    distanceModel = new DistanceModel();
                    distanceModel.setDuration(jsonDuration.getString("text"));
                    routineInfo.put(productData.getLocation(), distanceModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routineInfo;
        }
        return null;
    }
    private Observable<Map> getRoutineObservable() {
        return Observable.defer(new Func0<Observable<Map>>() {
            @Override
            public Observable<Map> call() {
                Observable<Map> observable = null;
                try {
                    observable = Observable.just(getRoutineInfo());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                return observable;
            }
        });
    }

    private void subsribeData(final TextView tvArrivetime){
        getRoutineObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map map) {
                        for(Map.Entry<String, DistanceModel> entry : routineInfo.entrySet()) {
                            tvArrivetime.setText(entry.getValue().getDuration());
                        }
                    }
                });
    }

}
