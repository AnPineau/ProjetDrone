package com.example.projetdrone;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentConnexion extends Fragment {

    private FragmentManager fm;
    private Fragment fragmentConnexion;
    private Fragment fragmentVue1;
    private EditText et_ip, et_port;

    public FragmentConnexion() {
        // constructeur vide requis ne pas enlever
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_connexion, container, false);

        et_ip = view.findViewById(R.id.et_ip);
        et_port = view.findViewById(R.id.et_port);
        Button button_connexion = view.findViewById(R.id.button_connexion);
        fm = ((MainActivity)getActivity()).fm;
        fragmentConnexion = ((MainActivity)getActivity()).fragmentConnexion;
        fragmentVue1 = ((MainActivity)getActivity()).fragmentVue1;

        button_connexion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (et_ip.getText().toString().equals("") || et_port.getText().toString().equals("")) {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(), "Please fill the Adress and port slots", Toast.LENGTH_SHORT);
                    t.show();
                } else {
                    ((MainActivity)getActivity()).IP = et_ip.getText().toString();
                    ((MainActivity)getActivity()).port = Integer.parseInt(et_port.getText().toString());
                    ((MainActivity)getActivity()).active = fragmentVue1;
                    Log.d("connexion", "fragmentConnexion IP, port: "+et_ip.getText().toString()+", "+et_port.getText().toString());
                    fm.beginTransaction().add(R.id.fragmentcontainer, fragmentVue1, "1").commit();
                    fm.beginTransaction().hide(fragmentConnexion).commit();
                }
            }
        });

        return view;
    } // onCreateView


}
