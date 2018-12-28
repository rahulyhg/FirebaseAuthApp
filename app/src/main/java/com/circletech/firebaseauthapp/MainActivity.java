package com.circletech.firebaseauthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.circletech.firebaseauthapp.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding vMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
       // ListView vListView=findViewById(R.id.auth_lists);
        vMainBinding.authLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pAdapterView, View pView, int pI, long pL) {
                Toast.makeText(MainActivity.this, "position: "+pI, Toast.LENGTH_SHORT).show();
                switch (pI){
                    case 2:startActivity(new Intent(MainActivity.this,EmailPassword.class));
                        break;
                }
            }
        });

    }
}
