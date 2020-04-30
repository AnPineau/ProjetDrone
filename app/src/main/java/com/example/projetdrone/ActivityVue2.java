package com.example.projetdrone;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
//import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;

import static com.example.projetdrone.Util.createGPRMCTrame;

public class ActivityVue2  extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    LatLng lastPos = null;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Polyline> polylines = new ArrayList<>();
    ArrayList<Position> trajectoire = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue2);
        setTitle("Vue 2");

        Button btnVue1 =(Button)findViewById(R.id.BtnEnvoyer);
        btnVue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerSocket welcomeSocket = null;
                try {
                    String trame;
                    welcomeSocket = new ServerSocket(55556);
                    welcomeSocket.setSoTimeout(5000);
                    Socket connectionSocket = welcomeSocket.accept();
                    PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                    for(int i =0; i<trajectoire.size();i++){
                        trame = createGPRMCTrame(185757.550f, trajectoire.get(i).getLatitude(),trajectoire.get(i).getLongitude(),trajectoire.get(i).getVitesse(),150318);
                        out.println(trame);
                        Thread.sleep(500);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Centre la camera sur la Rochelle
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.14, -1.16), 12.0f));
        for(int i=0; i<trajectoire.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng=new LatLng(trajectoire.get(i).getLatitude(), trajectoire.get(i).getLongitude());
            markerOptions.position(latLng);
            markers.add(map.addMarker(markerOptions));
            if(lastPos!=null){
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
                if(lastPos!=null){
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityVue2.this);
                final EditText input = new EditText(ActivityVue2.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(Double.toString(trajectoire.get(index).getVitesse()));
                dialog.setView(input);
                dialog.setNegativeButton("Changer vitesse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        trajectoire.get(index).setVitesse(Double.parseDouble(input.getText().toString()));
                    }
                });
                dialog.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(markers.size()==1){
                            markers.remove(marker);
                            marker.remove();
                            lastPos=null;
                        }else if(index!=0 && index!=markers.size()-1){
                            markers.remove(marker);
                            marker.remove();
                            polylines.get(index-1).remove();
                            polylines.remove(index-1);
                            polylines.get(index-1).remove();
                            polylines.set(index-1,map.addPolyline(new PolylineOptions()
                                    .add(markers.get(index-1).getPosition(), markers.get(index).getPosition())
                                    .width(5)
                                    .color(Color.RED)));
                        }else if(index==0){
                            markers.remove(marker);
                            marker.remove();
                            polylines.get(0).remove();
                            polylines.remove(0);
                        }else{
                            markers.remove(marker);
                            marker.remove();
                            polylines.get(index-1).remove();
                            polylines.remove(index-1);
                            lastPos=markers.get(markers.size()-1).getPosition();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }


}
