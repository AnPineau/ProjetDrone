package com.example.projetdrone;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.fragment.app.Fragment;


public class FragmentVue2 extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    LatLng previousPos = null;
    Marker marker;

    public FragmentVue2() {
        // constructeur vide requis ne pas enlever
    }


    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_vue2, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (Bateau.trajectoire != null) {
            for (Position pos : Bateau.trajectoire) {
                LatLng lastPos = new LatLng(pos.getLatitude(), pos.getLongitude());
                if (previousPos != null) {

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

            h.postDelayed(new Runnable() {
                public void run() {
                    //Code executé toute les  5 secondes
                    LatLng lastPos = new LatLng(Bateau.getLastPosition().getLatitude(), Bateau.getLastPosition().getLongitude());
                    if (previousPos != null) {

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

    } // onMapReady
}

