package com.example.projetdrone;

import android.os.Bundle;
/*import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;*/
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


/*

    Hierarchie de l'appli :
        - MainActivity
            > FrameLayout : Layout dans lequel les fragments sont injectes
            > BottomNavigationView : Barre de navigation en bas de l'appli qui
                 permet de switch entre les fragments affiches

            * FragmentVue2 contient lui-même un fragment qui est utilise pour afficher la Map
                  (c'est la facon normale de googlemap)

 */


public class MainActivity extends AppCompatActivity {
    Connection server;
    // ------------ Ici on declare les fragments et le fragment manager (FragmentVue1 desactive parce qu'il crash)
    Fragment fragmentVue1;
    final Fragment fragmentVue2 = new FragmentVue2();
    final Fragment fragmentVue3 = new FragmentVue3();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentVue1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Connection a NMEA simulator
        try {
            Log.d("TCP Server", "Create connection ...");
            server = new Connection("188.213.28.206", 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentVue1 = new FragmentVue1(server);


        // ---------- On recupere la barre de navigation en bas et on lui assigne un ItemListener qu'on cree plus bas
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* ---------- On demarre tous les fragments et on cache (avec hide) ceux qui sont pas selectionnes
            dans la barre de navigation, ca permet de conserver les modifications sur un fragment quand on en change.
            Exemple: si on va a un endroit sur une map, qu'on change de fragment et qu'on revient sur la map plus
            tard, l'endroit sur la map sera conserve car le fragment n'a jamais ete ferme, seulement cache      */

        fm.beginTransaction().add(R.id.fragmentcontainer, fragmentVue2, "2").hide(fragmentVue2).commit();
        fm.beginTransaction().add(R.id.fragmentcontainer, fragmentVue3, "3").hide(fragmentVue3).commit();
        fm.beginTransaction().add(R.id.fragmentcontainer, fragmentVue1, "1").commit();

    } // onCreate

    // --------- Le navigation item listener utilise plus haut pour la barre de navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_vue1:
                    fm.beginTransaction().hide(active).show(fragmentVue1).commit();
                    active = fragmentVue1;
                    //Toast.makeText(getApplicationContext(), "Vue 1 désactivée", Toast.LENGTH_SHORT).show();
                    //return false;
                    return true;

                case R.id.navigation_vue2:
                    fm.beginTransaction().hide(active).show(fragmentVue2).commit();
                    active = fragmentVue2;
                    return true;

                case R.id.navigation_vue3:
                    fm.beginTransaction().hide(active).show(fragmentVue3).commit();
                    active = fragmentVue3;
                    return true;
            }
            return false;
        }
    }; // Navigation Item Listener

}

