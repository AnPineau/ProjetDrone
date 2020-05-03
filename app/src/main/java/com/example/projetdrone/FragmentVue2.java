package com.example.projetdrone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.SensorEventListener;

import static android.content.Context.SENSOR_SERVICE;


public class FragmentVue2 extends Fragment implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap map;
    private Marker marker;
    private boolean ready=false;

    public FragmentVue2() {
        // constructeur vide requis ne pas enlever
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_vue2, container, false);

        /*SensorManager sensorManager=(SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        Sensor accel=sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);*/

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        JoystickView joystick = view.findViewById(R.id.joystick);
        joystick.setFixedCenter(false);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                Log.d("joystick", "Angle, strength: "+angle+", "+strength);
                if (ready) {
                    double Vx = Math.cos(Math.toRadians(angle));
                    double Vy = Math.sin(Math.toRadians(angle));
                    double vitesse = 0.0003;
                    Log.d("joystick", "Vx, Vy: "+Vx+", "+Vy);
                    if (marker != null) {
                        double lat = marker.getPosition().latitude + (vitesse * Vy * ((double)strength/100));
                        double lon = marker.getPosition().longitude + (vitesse * Vx * ((double)strength/100));
                        marker.setPosition(new LatLng(lat, lon));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),map.getCameraPosition().zoom));
                    }
                }
            }
        });


        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.147780, -1.168557), 16.0f));
        map = googleMap;
        // Map en mode Hybrid et Zoom sur le port des minimes
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.1464, -1.1727), 14f));

        //Centre la camera sur la Rochelle
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(46.1464, -1.1727))
                .title("Bateau"));
        ready = true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (ready == true && this.isVisible()) {
            //avancer / reculer
            /*double axisX = (double) sensorEvent.values[0] * 0.001;

            //droite / gauche
            double axisY = (double) sensorEvent.values[1] * 0.001;

            double axisZ = (double) sensorEvent.values[2] * 0.001;

            //((TextView)findViewById(R.id.axeX)).setText(""+axisX);
            //((TextView)findViewById(R.id.axeY)).setText(""+axisY);
            //((TextView)findViewById(R.id.axeZ)).setText(""+axisZ);
            if (marker != null) {
                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),map.getCameraPosition().zoom));
                marker.setPosition(new LatLng(marker.getPosition().latitude - axisY, marker.getPosition().longitude - axisX));
            }*/
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
