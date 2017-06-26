package com.example.zhang.productapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrew Zhang on 6/23/2017.
 */

public class LocationModel implements Parcelable
{
    @SerializedName("Name")
    private String location;

    @SerializedName("Address")
    private String address;

    @SerializedName("ArrivalTime")
    private String arrivalTime;

    @SerializedName("Latitude")
    private String locLatitude;

    @SerializedName("Longitude")
    private String locLongitude;

    protected LocationModel(Parcel in) {
        location = in.readString();
        address = in.readString();
        arrivalTime = in.readString();
        locLatitude = in.readString();
        locLongitude= in.readString();

    }

    public LocationModel(String location, String address, String arrivalTime, String locLatitude, String locLongitude) {
        this.location = location;
        this.address = address;
        this.arrivalTime = arrivalTime;
        this.locLatitude = locLatitude;
        this.locLongitude = locLongitude;
    }


    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    public String getLocLatitude() {
        return locLatitude;
    }

    public void setLocLatitude(String locLatitude) {
        this.locLatitude = locLatitude;
    }

    public String getLocLongitude(){
        return locLongitude;
    }

    public void setLocLongitude(String locLongitude){
        this.locLongitude = locLongitude;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.location);
        parcel.writeString(this.address);
        parcel.writeString(this.arrivalTime);
        parcel.writeString(this.locLatitude);
        parcel.writeString(this.locLongitude);
    }
}
