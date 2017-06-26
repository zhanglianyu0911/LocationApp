package com.example.zhang.productapp.model;

/**
 * Created by Andrew Zhang on 6/23/2017.
 */


public class DistanceModel {
    private Double currLat;
    private Double currLong;
    private String distance;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Double getCurrLat() {
        return currLat;
    }

    public void setCurrLat(Double currLat) {
        this.currLat = currLat;
    }

    public Double getCurrLong() {
        return currLong;
    }

    public void setCurrLong(Double currLong) {
        this.currLong = currLong;
    }
}
