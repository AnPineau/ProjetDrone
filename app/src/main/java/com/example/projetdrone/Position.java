package com.example.projetdrone;


public class Position {
    public double latitude;
    public double longitude;

    public Position(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

}
