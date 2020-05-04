package com.example.projetdrone;

import java.util.ArrayList;


public  class Bateau {
    public ArrayList<Position> trajectoire;
    public double vitesse;

    public Bateau(){
        trajectoire = new ArrayList<>();
        vitesse=10;
    }

    public void ajouterPosition(Position pos){
        trajectoire.add(pos);
    }

    public ArrayList<Position> getTrajectoire(){
        return trajectoire;
    }
    public double getVitesse(){
        return vitesse;
    }
    public void setVitesse(double vit){
        vitesse=vit;
    }

    public Position getLastPosition(){
            return trajectoire.get(trajectoire.size()-1);
    }

    @Override
    public String toString() {
        return "Bateau{" +
                "trajectoire=" + trajectoire +
                '}';
    }
}
