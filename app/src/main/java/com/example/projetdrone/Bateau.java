package com.example.projetdrone;

import java.util.ArrayList;

/**
 * 
 */

public  class Bateau {
    public ArrayList<Position> trajectoire;

    public Bateau(){
        trajectoire = new ArrayList<>();
    }

    public void ajouterPosition(Position pos){
        trajectoire.add(pos);
    }

    public ArrayList<Position> getTrajectoire(){
        return trajectoire;
    }

    public Position getLastPosition(){
        if(trajectoire.size()>0)
            return trajectoire.get(trajectoire.size()-1);
        else
            return new Position(0,0,0);
    }

    @Override
    public String toString() {
        return "Bateau{" +
                "trajectoire=" + trajectoire +
                '}';
    }
}
