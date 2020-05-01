package com.example.projetdrone;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Objects;


public class FragmentVue1 extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng previousPos = null;
    private Marker marker;
    private Bateau bateau;
    private Connection server;

    public FragmentVue1(){
    }


    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_vue1, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //bateau = ((MainActivity) Objects.requireNonNull(getActivity())).server.bateau;
        map = googleMap;
        // Map en mode Hybrid et Zoom sur le port des minimes
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.1464, -1.1727), 14f));
        /*if (bateau.trajectoire != null) {
            for (Position pos : this.server.bateau.trajectoire) {
                LatLng lastPos = new LatLng(pos.getLatitude(), pos.getLongitude());
                if (previousPos != null) {

                    map.addPolyline(new PolylineOptions()
                            .add(previousPos, lastPos)
                            .width(5)
                            .color(Color.RED));
                }
                previousPos = lastPos;
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.server.bateau.getLastPosition().getLatitude(), this.server.bateau.getLastPosition().getLongitude()), 15.0f));
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(this.server.bateau.getLastPosition().getLatitude(), this.server.bateau.getLastPosition().getLongitude()))
                    .title("Bateau"));

            final Handler h = new Handler();
            h.postDelayed(new Runnable() {

                public void run() {
                    //Code executé toute les  5 secondes
                    LatLng lastPos = new LatLng(server.bateau.getLastPosition().getLatitude(), server.bateau.getLastPosition().getLongitude());
                    if (previousPos != null) {

                        map.addPolyline(new PolylineOptions()
                                .add(previousPos, lastPos)
                                .width(5)
                                .color(Color.RED));
                    }
                    //change la position du marqueur
                    marker.setPosition(lastPos);

                    h.postDelayed(this, 5000);
                    previousPos = lastPos;
                }
            }, 5000);
        }*/

    } // onMapReady
}

