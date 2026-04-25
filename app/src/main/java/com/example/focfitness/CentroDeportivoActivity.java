package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
public class CentroDeportivoActivity extends AppCompatActivity {
    LinearLayout btnPerfil;
    android.widget.FrameLayout btnReservas, btnCalendario, btnNormas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_deportivo);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnReservas = findViewById(R.id.btnReservas);
        btnCalendario = findViewById(R.id.btnCalendario);
        btnNormas = findViewById(R.id.btnNormas);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(CentroDeportivoActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnReservas.setOnClickListener(v -> {
            Intent intent = new Intent(CentroDeportivoActivity.this, ReservasActivity.class);
            startActivity(intent);
        });

        //TODO-06 Crear el calendario de eventos y las normas
        btnCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(CentroDeportivoActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });

        btnNormas.setOnClickListener(v -> {
            Intent intent = new Intent(CentroDeportivoActivity.this, NormasActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PerfilHeaderHelper.cargarFotoPerfil(this);
    }
}
