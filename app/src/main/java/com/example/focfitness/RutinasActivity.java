package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class RutinasActivity extends AppCompatActivity {

    android.widget.FrameLayout btnTrenSuperior, btnTrenInferior, btnPecho, btnEspalda, btnCardio, btnCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        btnTrenSuperior = findViewById(R.id.btnTrenSuperior);
        btnTrenInferior = findViewById(R.id.btnTrenInferior);
        btnPecho = findViewById(R.id.btnPecho);
        btnEspalda = findViewById(R.id.btnEspalda);
        btnCardio = findViewById(R.id.btnCardio);
        btnCore = findViewById(R.id.btnCore);

        btnTrenSuperior.setOnClickListener(v -> abrirEjercicios("Tren Superior"));
        btnTrenInferior.setOnClickListener(v -> abrirEjercicios("Tren Inferior"));
        btnPecho.setOnClickListener(v -> abrirEjercicios("Pecho"));
        btnEspalda.setOnClickListener(v -> abrirEjercicios("Espalda"));
        btnCardio.setOnClickListener(v -> abrirEjercicios("Cardio"));
        btnCore.setOnClickListener(v -> abrirEjercicios("Core"));
    }

    private void abrirEjercicios(String categoria) {
        Intent intent = new Intent(this, EjerciciosRutinaActivity.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }
}
