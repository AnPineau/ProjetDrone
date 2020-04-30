package com.example.projetdrone;

import java.util.ArrayList;

/**
 * 
 */

public class Bateau {
    public static ArrayList<Position> trajectoire;

    public Bateau(){
        trajectoire = new ArrayList<>();
    }

    public void ajouterPosition(Position pos){
        trajectoire.add(pos);
    }

    public static ArrayList<Position> getTrajectoire(){
        return trajectoire;
    }

    public static Position getLastPosition(){
        return trajectoire.get(trajectoire.size()-1);
    }
}
