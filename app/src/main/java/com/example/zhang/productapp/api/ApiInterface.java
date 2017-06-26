package com.example.zhang.productapp.api;

import com.example.zhang.productapp.model.LocationModel;

import java.util.List;

;
import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by Andrew Zhang on 6/23/2017.
 */

public interface ApiInterface {

    @GET("api/Locations")
    Call<List<LocationModel>> getProductData();
}
