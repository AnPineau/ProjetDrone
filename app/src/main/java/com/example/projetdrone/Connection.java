package com.example.projetdrone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.example.projetdrone.Util.GoogleMapToNMEA;
import static com.example.projetdrone.Util.NMEAtoGoogleMap;
import static com.example.projetdrone.Util.calculChecksum;

public class Connection implements Runnable {
    private Socket socket;
    public Bateau bateau;

    public Connection(String serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.bateau = new Bateau();
        Thread th = new Thread(this);
        th.start();
    }

    @Override
    public void run()  {
        String fullTrame = null;
        String[] trame;
        while (true) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while (!in.ready()) {}
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fullTrame = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            trame = fullTrame.split(",");

            //Vérifie si le checksum est valide si c'est le cas alors ajout de la position dans le tableau contenant la trajectoire du bateau
            String checksum = calculChecksum(fullTrame);
            if(checksum.equals(trame[12].substring(1,3))){
                bateau.ajouterPosition(new Position(NMEAtoGoogleMap(trame[3], trame[4]), NMEAtoGoogleMap(trame[5], trame[6]),0));
            }

        }
    }



}
