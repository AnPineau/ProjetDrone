package com.example.projetdrone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class FragmentVue3 extends Fragment implements OnMapReadyCallback {

    private Handler animateBoat;
    ImageButton btn_speed;
    private GoogleMap map;
    private LatLng lastPos = null;
    private Bateau boat;
    private Marker marker_boat;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<Position> trajectoire = new ArrayList<>();
    private File waypointsXML;
    private TextView tv_lat, tv_long;

    public FragmentVue3() {
        // constructeur vide requis ne pas enlever
    }

    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        //boat=new Bateau();
        boat=((MainActivity)getActivity()).bateauWaypoints;

        View view = inflater.inflate(R.layout.fragment_vue3, container, false);
        //boat=new Bateau();
        boat=((MainActivity)getActivity()).bateauWaypoints;


        btn_speed = view.findViewById(R.id.speed);
            btn_speed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //server.bateau.setVitesse()
                    //final int index = markers.indexOf(marker);
                    //Créé une fenetre de dialogue
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setText(Double.toString(boat.getVitesse()));
                    dialog.setView(input);
                    dialog.setNegativeButton("Changer vitesse", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boat.setVitesse(Double.parseDouble(input.getText().toString()));
                        }
                    });
                    dialog.show();
                }
            });
        //}

        tv_lat = view.findViewById(R.id.tv_lat_vue1);
        tv_long = view.findViewById(R.id.tv_long_vue1);

        MapFragment mapFragment = (MapFragment) Objects.requireNonNull(getActivity()).getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        animateBoat=new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(boat.trajectoire.size()>1){
                    if((Math.round(boat.trajectoire.get(1).longitude*1000)==Math.round(boat.trajectoire.get(0).longitude*1000)) &&
                            Math.round(boat.trajectoire.get(1).latitude*1000)==Math.round(boat.trajectoire.get(0).latitude*1000) ){
                        boat.trajectoire.remove(0);
                    } else {
                        if(boat.trajectoire.get(1).latitude < boat.trajectoire.get(0).latitude){
                            if(boat.trajectoire.get(1).latitude > boat.trajectoire.get(0).latitude-0.00001*boat.vitesse ){
                                boat.trajectoire.get(0).latitude=boat.trajectoire.get(1).latitude;
                            } else {
                                boat.trajectoire.get(0).latitude=boat.trajectoire.get(0).latitude-0.00001*boat.vitesse;
                            }
                        } else if(boat.trajectoire.get(1).latitude > boat.trajectoire.get(0).latitude) {
                            if(boat.trajectoire.get(1).latitude < boat.trajectoire.get(0).latitude+0.00001*boat.vitesse ){
                                boat.trajectoire.get(0).latitude=boat.trajectoire.get(1).latitude;
                            } else {
                                boat.trajectoire.get(0).latitude = boat.trajectoire.get(0).latitude + 0.00001 * boat.vitesse;
                            }
                        }
                        if(boat.trajectoire.get(1).longitude < boat.trajectoire.get(0).longitude){
                            if(boat.trajectoire.get(1).longitude > boat.trajectoire.get(0).longitude-0.00001*boat.vitesse){
                                boat.trajectoire.get(0).longitude=boat.trajectoire.get(1).longitude;
                            } else {
                                boat.trajectoire.get(0).longitude=boat.trajectoire.get(0).longitude-0.00001*boat.vitesse;
                            }
                        } else if(boat.trajectoire.get(1).longitude > boat.trajectoire.get(0).longitude){
                            if(boat.trajectoire.get(1).longitude < boat.trajectoire.get(0).longitude+0.00001*boat.vitesse){
                                boat.trajectoire.get(0).longitude=boat.trajectoire.get(1).longitude;
                            } else {
                                boat.trajectoire.get(0).longitude=boat.trajectoire.get(0).longitude+0.00001*boat.vitesse;
                            }
                        }
                    }
                }
                marker_boat.setPosition(new LatLng(boat.trajectoire.get(0).latitude,boat.trajectoire.get(0).longitude));
                animateBoat.postDelayed(this, 300);

                tv_lat.setText(("lat: "+ String.format("%.4f", marker_boat.getPosition().latitude)));
                tv_long.setText(("lon: "+ String.format("%.4f", marker_boat.getPosition().longitude)));
            }
        };

        animateBoat.postDelayed(runnable, 1000);


        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Map en mode Hybrid et Zoom sur le port des minimes
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // faire un zoom ici
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(boat.trajectoire.get(0).latitude, boat.trajectoire.get(0).longitude), 14f));
        marker_boat = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(45))
                .position(new LatLng(boat.trajectoire.get(0).latitude, boat.trajectoire.get(0).longitude))
                .title("Bateau"));

        for (int i = 0; i < trajectoire.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(trajectoire.get(i).getLatitude(), trajectoire.get(i).getLongitude());
            markerOptions.position(latLng);
            boat.trajectoire.add(new Position(trajectoire.get(i).latitude,trajectoire.get(i).longitude));
            markers.add(map.addMarker(markerOptions));
            if (lastPos != null) {
                polylines.add(map.addPolyline(new PolylineOptions()
                        .add(lastPos, latLng)
                        .width(5)
                        .color(Color.RED)));
            }
            lastPos = latLng;
        }

        //Listener sur le clic sur la googleMap
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                trajectoire.add(new Position(latLng.latitude, latLng.longitude));
                boat.trajectoire.add(new Position(latLng.latitude,latLng.longitude));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                //Centre la camera sur position cliquer
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                markers.add(map.addMarker(markerOptions));


                //Trace un trait entre les 2 derniers waypoint créé
                if (lastPos != null) {
                    polylines.add(map.addPolyline(new PolylineOptions()
                            .add(lastPos, latLng)
                            .width(5)
                            .color(Color.RED)));
                }
                lastPos = latLng;
                // ecriture dans xml apres ajout du waypoint
                try {
                    writeWaypoints();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final int index = markers.indexOf(marker);
                // Fenetre de dialogue
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage(R.string.dialog_question);
                dialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }
                });
                dialog.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!marker.equals(marker_boat)) {
                            if (markers.size() == 1) {
                                markers.remove(marker);
                                marker.remove();
                                lastPos = null;
                            } else if (index != 0 && index != markers.size() - 1) {
                                markers.remove(marker);
                                marker.remove();
                                polylines.get(index - 1).remove();
                                polylines.remove(index - 1);
                                polylines.get(index - 1).remove();
                                polylines.set(index - 1, map.addPolyline(new PolylineOptions()
                                        .add(markers.get(index - 1).getPosition(), markers.get(index).getPosition())
                                        .width(5)
                                        .color(Color.RED)));
                            } else if (index == 0) {
                                markers.remove(marker);
                                marker.remove();
                                polylines.get(0).remove();
                                polylines.remove(0);
                            } else {
                                markers.remove(marker);
                                marker.remove();
                                polylines.get(index - 1).remove();
                                polylines.remove(index - 1);
                                lastPos = markers.get(markers.size() - 1).getPosition();
                            }
                            for (int i = 0; i < boat.trajectoire.size(); i++) {
                                if (trajectoire.get(index).longitude == boat.trajectoire.get(i).longitude && trajectoire.get(index).latitude == boat.trajectoire.get(i).latitude) {
                                    boat.trajectoire.remove(i);
                                }
                            }
                            trajectoire.remove(index);
                            // ecriture dans le xml apres suppression du waypoint
                            try {
                                writeWaypoints();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.marker_deletion, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    } // onMapReady

    private void writeWaypoints() throws IOException {
        waypointsXML = new File(Objects.requireNonNull(getActivity()).getFilesDir(), "waypoints.xml");
        try (FileOutputStream fos = new FileOutputStream(waypointsXML, false)) {
            fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
            fos.write("<gpx>\n".getBytes());
            fos.write("<name>lesminimes</name>\n".getBytes());
            for (Position p : trajectoire) {
                fos.write(("<wpt lat=\"" + p.latitude + "\" lon=\"" + p.longitude + "\">\n").getBytes());
                fos.write("</wpt>\n".getBytes());
            }
            fos.write("</gpx>\n".getBytes());
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        // Lecture pour checker l'ecriture
        int length = (int) waypointsXML.length();
        byte[] bytes = new byte[length];
        try (FileInputStream in = new FileInputStream(waypointsXML)) {
            in.read(bytes);
        }
        Log.d("ECRITURE", new String(bytes));
    }

}
