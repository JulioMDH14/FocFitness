package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
public class AboloteActivity extends AppCompatActivity {

    ImageView btnMenu;
    LinearLayout btnPerfil;
    FrameLayout btnReservas, btnCDeportivo, btnGimnasio, btnGaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albolote);

        btnMenu = findViewById(R.id.btnMenu);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnReservas = findViewById(R.id.btnReservas);
        btnCDeportivo = findViewById(R.id.btnCDeportivo);
        btnGimnasio = findViewById(R.id.btnGimnasio);
        btnGaleria = findViewById(R.id.btnGaleria);

        //TODO-04 Crear Activities de las diferentes secciones
        /*
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnReservas.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, MisReservasActivity.class);
            startActivity(intent);
        });

        btnGimnasio.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, ReservasGimnasioActivity.class);
            startActivity(intent);
        });
        */

        btnCDeportivo.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, ReservasActivity.class);
            startActivity(intent);
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(AboloteActivity.this, GaleriaActivity.class);
            startActivity(intent);
        });
    }
}
