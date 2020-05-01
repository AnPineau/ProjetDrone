package com.example.projetdrone;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.SensorEventListener;

import static android.content.Context.SENSOR_SERVICE;


public class FragmentVue3 extends Fragment implements OnMapReadyCallback, SensorEventListener {
    GoogleMap map;
    Marker marker;
    boolean ready=false;

    public FragmentVue3() {
        // constructeur vide requis ne pas enlever
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sensorManager=(SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        Sensor accel=sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        View view = inflater.inflate(R.layout.fragment_vue3, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.147780, -1.168557), 16.0f));
        map = googleMap;
        // Map en mode Hybrid et Zoom sur le port des minimes
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.1464, -1.1727), 14f));

        //Centre la camera sur la Rochelle
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(46.14, -1.16))
                .title("Bateau"));
        ready = true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (ready == true && this.isVisible()) {
            //avancer / reculer
            double axisX = (double) sensorEvent.values[0] * 0.001;

            //droite / gauche
            double axisY = (double) sensorEvent.values[1] * 0.001;

            double axisZ = (double) sensorEvent.values[2] * 0.001;

            //((TextView)findViewById(R.id.axeX)).setText(""+axisX);
            //((TextView)findViewById(R.id.axeY)).setText(""+axisY);
            //((TextView)findViewById(R.id.axeZ)).setText(""+axisZ);
            if (marker != null) {
                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),map.getCameraPosition().zoom));
                marker.setPosition(new LatLng(marker.getPosition().latitude - axisY, marker.getPosition().longitude - axisX));
            }
        /*
        if(map!=null){
            axisX=((CameraPosition)map.getCameraPosition()).target.longitude-axisX;
            axisY=((CameraPosition)map.getCameraPosition()).target.latitude+axisY;
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(axisY, axisX), 12.0f));
        }
        */
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
