package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class HomeActivity extends AppCompatActivity {

    FrameLayout btnAbolote, btnLaZubia, btnMaracena, btnArmilla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAbolote.findViewById(R.id.btnAlbolote);
        btnLaZubia.findViewById(R.id.btnLaZubia);
        btnMaracena.findViewById(R.id.btnMaracena);
        btnArmilla.findViewById(R.id.btnArmilla);

        //TODO-03 Crear las Activity de cada pueblo
        /*
        btnAbolote.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AlboloteActivity.class);
            startActivity(intent);
        });

        btnLaZubia.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ZubiaActivity.class);
            startActivity(intent);
        });

        btnMaracena.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MaracenaActivity.class);
            startActivity(intent);
        });

        btnArmilla.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Armillaactivity.class);
            startActivity(intent);
        });
        */

    }
}
