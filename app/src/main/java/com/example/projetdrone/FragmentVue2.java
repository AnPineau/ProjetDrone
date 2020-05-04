package com.example.projetdrone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.text.InputType;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.SensorEventListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;


public class FragmentVue2 extends Fragment implements OnMapReadyCallback, SensorEventListener {
    private Handler animateBoat;
    ImageButton btn_speed;
    private Bateau bateau;
    private GoogleMap map;
    private Marker marker;
    private boolean ready=false;
    private TextView tv_lat, tv_long;

    public FragmentVue2() {
        // constructeur vide requis ne pas enlever
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //final Bateau bateau=new Bateau();
        bateau=((MainActivity)getActivity()).bateauPilot;

        View view = inflater.inflate(R.layout.fragment_vue2, container, false);
        //final Bateau bateau=new Bateau();
        bateau=((MainActivity)getActivity()).bateauPilot;

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
                input.setText(Double.toString(bateau.getVitesse()));
                dialog.setView(input);
                dialog.setNegativeButton("Changer vitesse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bateau.setVitesse(Double.parseDouble(input.getText().toString()));
                    }
                });
                dialog.show();
            }
        });

        tv_lat = view.findViewById(R.id.tv_lat_vue1);
        tv_long = view.findViewById(R.id.tv_long_vue1);

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
                    double vitesse = 0.00003*bateau.vitesse;
                    Log.d("joystick", "Vx, Vy: "+Vx+", "+Vy);
                    if (marker != null) {
                        double lat = marker.getPosition().latitude + (vitesse * Vy * ((double)strength/100));
                        double lon = marker.getPosition().longitude + (vitesse * Vx * ((double)strength/100));
                        marker.setPosition(new LatLng(lat, lon));
                        bateau.getTrajectoire().get(0).latitude=lat;
                        bateau.getTrajectoire().get(0).longitude=lon;
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),map.getCameraPosition().zoom));

                        tv_lat.setText(("lat: "+ String.format("%.4f", marker.getPosition().latitude)));
                        tv_long.setText(("lon: "+ String.format("%.4f", marker.getPosition().longitude)));
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
                .icon(BitmapDescriptorFactory.defaultMarker(45))
                .position(new LatLng(bateau.trajectoire.get(0).latitude, bateau.trajectoire.get(0).longitude))
                .title("Bateau"));
        ready = true;

        animateBoat=new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //marker.setPosition(new LatLng(bateau.trajectoire.get(0).latitude,bateau.trajectoire.get(0).longitude));
                animateBoat.postDelayed(this, 300);
            }
        };

        animateBoat.postDelayed(runnable, 1000);

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
