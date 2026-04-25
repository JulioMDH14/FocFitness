package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class GimnasioActivity extends AppCompatActivity {

    ImageButton btnMenu;
    LinearLayout btnPerfil;
    android.widget.FrameLayout btnReservar, btnClases, btnSeguimiento, btnRutinas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gimnasio);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnReservar = findViewById(R.id.btnReservar);
        btnClases = findViewById(R.id.btnClases);
        btnSeguimiento = findViewById(R.id.btnSeguimiento);
        btnRutinas = findViewById(R.id.btnRutinas);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(GimnasioActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnReservar.setOnClickListener(v -> {
            Intent intent = new Intent(GimnasioActivity.this, ReservaGimnasioActivity.class);
            startActivity(intent);
        });

        btnClases.setOnClickListener(v -> {
            Intent intent = new Intent(GimnasioActivity.this, ClasesActivity.class);
            startActivity(intent);
        });

        btnSeguimiento.setOnClickListener(v -> {
            Intent intent = new Intent(GimnasioActivity.this, SeguimientoActivity.class);
            startActivity(intent);
        });

        btnRutinas.setOnClickListener(v -> {
            Intent intent = new Intent(GimnasioActivity.this, RutinasActivity.class);
            startActivity(intent);
        });
    }
}
