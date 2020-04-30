package com.example.projetdrone;



public class Position {
    private double latitude;
    private double longitude;
    private double vitesse;

    public Position(double latitude, double longitude, double vitesse){
        this.latitude = latitude;
        this.longitude = longitude;
        this.vitesse = vitesse;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public double getVitesse(){ return this.vitesse; }

    public void setVitesse(double vitesse){ this.vitesse = vitesse; }
}
