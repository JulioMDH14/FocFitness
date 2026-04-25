package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
public class ArmillaActivity extends AppCompatActivity {

    ImageView btnMenu;
    LinearLayout btnPerfil;
    FrameLayout btnReservas, btnCDeportivo, btnGimnasio, btnGaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armilla);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnReservas = findViewById(R.id.btnReservas);
        btnCDeportivo = findViewById(R.id.btnCDeportivo);
        btnGimnasio = findViewById(R.id.btnGimnasio);
        btnGaleria = findViewById(R.id.btnGaleria);


        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnReservas.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });

        btnCDeportivo.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });

        btnGimnasio.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(ArmillaActivity.this, ConstruccionActivity.class);
            startActivity(intent);
        });
    }
}
