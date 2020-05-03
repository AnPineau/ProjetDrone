package com.example.projetdrone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class FragmentVue3 extends Fragment implements OnMapReadyCallback {

    ImageButton btn_speed;
    private GoogleMap map;
    private LatLng lastPos = null;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<Position> trajectoire = new ArrayList<>();
    private File waypointsXML;
    private Connection server;

    public FragmentVue3() {
        // constructeur vide requis ne pas enlever
    }

    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.server = new Connection("188.213.28.206", 3000);
            Log.d("TCP Server", "Create connection ...");
            System.out.println(this.server);
        } catch (Exception e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_vue3, container, false);

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
        //}

        MapFragment mapFragment = (MapFragment) Objects.requireNonNull(getActivity()).getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Map en mode Hybrid et Zoom sur le port des minimes
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // faire un zoom ici
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.1464, -1.1727), 20f));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.1464, -1.1727), 14f));

        for (int i = 0; i < trajectoire.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(trajectoire.get(i).getLatitude(), trajectoire.get(i).getLongitude());
            markerOptions.position(latLng);
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
                        trajectoire.remove(index);
                        // ecriture dans le xml apres suppression du waypoint
                        try {
                            writeWaypoints();
                        } catch (IOException e) {
                            e.printStackTrace();
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
