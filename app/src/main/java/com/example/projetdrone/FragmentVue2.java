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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class FragmentVue2 extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng lastPos = null;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<Position> trajectoire = new ArrayList<>();

    public FragmentVue2() {
        // constructeur vide requis ne pas enlever
    }

    @Override // onCreateView equivalent de onCreate mais pour les fragments, il doit retourner view
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_vue2, container, false);

        /*ServerSocket welcomeSocket = null;
        try {
            String trame;
            welcomeSocket = new ServerSocket(55556);
            welcomeSocket.setSoTimeout(5000);
            Socket connectionSocket = welcomeSocket.accept();
            PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
            for (int i = 0; i < trajectoire.size(); i++) {
                trame = createGPRMCTrame(185757.550f, trajectoire.get(i).getLatitude(), trajectoire.get(i).getLongitude(), trajectoire.get(i).getVitesse(), 150318);
                out.println(trame);
                Thread.sleep(500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /*getFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    } // onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Centre la camera sur la Rochelle
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.14, -1.16), 12.0f));
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
                trajectoire.add(new Position(latLng.latitude, latLng.longitude, 10));
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
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final int index = markers.indexOf(marker);
                //Créé une fenetre de dialogue
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
                    }
                });
                dialog.show();
                return true;
            }
        });
    } // onMapReady


}
