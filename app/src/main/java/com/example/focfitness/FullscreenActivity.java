package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FullscreenActivity extends AppCompatActivity {

    ImageView imagenGrande;
    ImageButton btnIzquierda, btnDerecha;

    int[] imagenes = {
            R.drawable.futbol_albolote,
            R.drawable.bpx1,
            R.drawable.pabellon_albolote,
            R.drawable.bpx3,
            R.drawable.tenis_albolote,
            R.drawable.vestuarios_albolote
    };

    int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        imagenGrande = findViewById(R.id.imagenGrande);
        btnIzquierda = findViewById(R.id.btnIzquierda);
        btnDerecha = findViewById(R.id.btnDerecha);

        posicion = getIntent().getIntExtra("posicion", 0);

        mostrarImagen();

        // Flecha derecha ➡️
        btnDerecha.setOnClickListener(v -> {
            if (posicion < imagenes.length - 1) {
                posicion++;
                mostrarImagen();
            }
        });

        // Flecha izquierda ⬅️
        btnIzquierda.setOnClickListener(v -> {
            if (posicion > 0) {
                posicion--;
                mostrarImagen();
            }
        });
    }

    private void mostrarImagen() {
        imagenGrande.setImageResource(imagenes[posicion]);
    }
}