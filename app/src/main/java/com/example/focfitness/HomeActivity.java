package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class HomeActivity extends AppCompatActivity {

    FrameLayout btnAbolote, btnLaZubia, btnMaracena, btnArmilla;
    LinearLayout btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAbolote = findViewById(R.id.btnAlbolote);
        btnLaZubia = findViewById(R.id.btnLaZubia);
        btnMaracena = findViewById(R.id.btnMaracena);
        btnArmilla = findViewById(R.id.btnArmilla);
        btnPerfil = findViewById(R.id.btnPerfil);

        //TODO-03 Crear las Activity de cada pueblo

        btnAbolote.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AboloteActivity.class);
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
            Intent intent = new Intent(HomeActivity.this, ArmillaActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PerfilHeaderHelper.cargarFotoPerfil(this);
    }
}
