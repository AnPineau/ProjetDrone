package com.example.projetdrone;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView tv_lat, tv_long;

    public FragmentVue1(){
    }


    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_vue1, container, false);
        //Connection a NMEA simulator
        try {
            this.server = new Connection("93.3.29.142", 3000);
            Log.d("TCP Server", "Create connection ...");
            System.out.println(this.server);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_lat = view.findViewById(R.id.tv_lat_vue1);
        tv_long = view.findViewById(R.id.tv_long_vue1);

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



        if(this.server.bateau.trajectoire.size() < 1 )
            {
                int compteur = 0;
                while (this.server.getSocket() == null )
                try {
                    this.server = new Connection("93.3.29.142", 3000);
                    compteur += 1;
                    if(compteur > 10)
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        while (this.server.bateau.trajectoire.size() < 1){
            Log.d("testDonnées", server.bateau.trajectoire.toString());

        }
        Log.d("testDonnées", server.bateau.trajectoire.toString());

        if (this.server.bateau.trajectoire != null && this.server.bateau.trajectoire.size() > 0) {
                for (Position pos : this.server.bateau.trajectoire) {
                    LatLng lastPos = new LatLng(pos.getLatitude(), pos. getLongitude());
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
                        tv_lat.setText(("lat: "+ String.format("%.4f", marker.getPosition().latitude)));
                        tv_long.setText(("lon: "+ String.format("%.4f", marker.getPosition().longitude)));

                        h.postDelayed(this, 5000);
                        previousPos = lastPos;
                    }
                }, 5000);
            }
    } // onMapReady

}

