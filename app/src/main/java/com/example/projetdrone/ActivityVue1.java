package com.example.projetdrone;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.fragment.app.FragmentActivity;


public class ActivityVue1  extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    LatLng previousPos= null;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue1);
        setTitle("Vue 1");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(Bateau.trajectoire != null){
            for(Position pos: Bateau.trajectoire){
                LatLng lastPos = new LatLng(pos.getLatitude(),pos.getLongitude());
                if(previousPos != null){

                    map.addPolyline(new PolylineOptions()
                            .add(previousPos, lastPos)
                            .width(5)
                            .color(Color.RED));
                }
                previousPos = lastPos;
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Bateau.getLastPosition().getLatitude(), Bateau.getLastPosition().getLongitude()), 15.0f));
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Bateau.getLastPosition().getLatitude(), Bateau.getLastPosition().getLongitude()))
                    .title("Bateau"));

            final Handler h = new Handler();
            final int delay = 5 * 1000;

            h.postDelayed(new Runnable(){
                public void run(){
                    //Code executé toute les  5 secondes
                    LatLng lastPos = new LatLng(Bateau.getLastPosition().getLatitude(), Bateau.getLastPosition().getLongitude());
                    if(previousPos != null){

                        map.addPolyline(new PolylineOptions()
                                .add(previousPos, lastPos)
                                .width(5)
                                .color(Color.RED));
                    }
                    //change la position du marqueur
                    marker.setPosition(lastPos);

                    h.postDelayed(this, delay);
                    previousPos = lastPos;
                }
            }, delay);
        }

    }
}

