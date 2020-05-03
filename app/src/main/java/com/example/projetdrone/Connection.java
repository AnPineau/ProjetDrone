package com.example.projetdrone;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection extends Thread implements Runnable {
    private Socket socket;
    public Bateau bateau;
    private volatile int port=3000;
    private volatile String IP= "1813.28.206";
    private volatile BufferedReader br;
    private volatile String Etat;
    private volatile FragmentVue1 f1;

    Connection(String url, int port){
        this.IP = url;
        this.port = port;
        this.bateau = new Bateau();
        Thread th = new Thread(this);
        th.start();
    }

    private boolean connexion(){
        try{
            this.socket = new Socket(IP,port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("connected", "CONNECTED");
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run()  {
        String fullTrame = null;
        String[] trame;
        while (true) {

            if (br == null || socket == null) {
                connexion();
            } else {
                try {
                    while (!br.ready()) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fullTrame = br.readLine();
                    Log.d("connected", fullTrame);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                trame = fullTrame.split(",");
                System.out.println(fullTrame);
                //Vérifie si le checksum est valide si c'est le cas alors ajout de la position dans le tableau contenant la trajectoire du bateau
                String checksum = Util.calculChecksum(fullTrame);
                Log.d("TCP Server", "TRY tram ...");

                if (checksum.equals(trame[9].substring(1, 3))) {
                    Log.d("TCP Server", "NMEA TRAM OK ...");

                    this.bateau.ajouterPosition(new Position(Util.NMEAtoGoogleMap(trame[3], trame[4]), Util.NMEAtoGoogleMap(trame[5], trame[6])));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Connection{" +
                "socket=" + socket +
                ", bateau=" + bateau +
                '}';
    }
}
