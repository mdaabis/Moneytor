package com.example.moneytor;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Fragment80_20 extends Fragment {

    int selectedElement = FetchData.selectedElement;
    TextView tv;

    public Fragment80_20() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment80_20, container, false);

        tv = (TextView) view.findViewById(R.id.investment80);
        return view;
    }

}
