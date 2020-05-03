package com.example.projetdrone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageButton;

import static android.content.Context.SENSOR_SERVICE;


public class FragmentVue2 extends Fragment implements OnMapReadyCallback, SensorEventListener {
    ImageButton btn_speed;
    private Connection server;
    private GoogleMap map;
    private Marker marker;
    private boolean ready=false;

    public FragmentVue2() {
        // constructeur vide requis ne pas enlever
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.server = new Connection("188.213.28.206", 3000);
            Log.d("TCP Server", "Create connection ...");
            System.out.println(this.server);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SensorManager sensorManager=(SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        Sensor accel=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        View view = inflater.inflate(R.layout.fragment_vue2, container, false);

        btn_speed = (ImageButton)view.findViewById(R.id.speed); //<< initialize here
        // set OnClickListener for Button here
        //if(btn_speed!=null) {
        btn_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //server.bateau.setVitesse()
                //final int index = markers.indexOf(marker);
                //Créé une fenetre de dialogue
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(Double.toString(server.bateau.getVitesse()));
                dialog.setView(input);
                dialog.setNegativeButton("Changer vitesse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        server.bateau.setVitesse(Double.parseDouble(input.getText().toString()));
                    }
                });
                dialog.show();
            }
        });

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
            double axisX = (double) sensorEvent.values[0] * 0.00001*server.bateau.vitesse;

            //droite / gauche
            double axisY = (double) sensorEvent.values[1] * 0.00001*server.bateau.vitesse;

            double axisZ = (double) sensorEvent.values[2] * 0.00001*server.bateau.vitesse;

            double longi;
            double lat;
            //((TextView)findViewById(R.id.axeX)).setText(""+axisX);
            //((TextView)findViewById(R.id.axeY)).setText(""+axisY);
            //((TextView)findViewById(R.id.axeZ)).setText(""+axisZ);
            if (marker != null) {
                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),map.getCameraPosition().zoom));
                //marker.setPosition(new LatLng(marker.getPosition().latitude - axisY, marker.getPosition().longitude - axisX));
                lat=marker.getPosition().latitude-axisX;
                longi=marker.getPosition().longitude+ axisY;
                marker.setPosition(new LatLng(lat,longi));
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
