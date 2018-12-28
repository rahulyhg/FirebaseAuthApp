package com.circletech.firebaseauthapp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class EventHandlers implements AdapterView.OnItemClickListener {


    @Override
    public void onItemClick(AdapterView<?> pAdapterView, View pView, int pI, long pL) {
        Toast.makeText(pView.getContext(), "Position:"+pI, Toast.LENGTH_SHORT).show();
    }
}
