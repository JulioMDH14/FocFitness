package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class GaleriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        ImageView img1 = findViewById(R.id.img1);
        ImageView img2 = findViewById(R.id.img2);
        ImageView img3 = findViewById(R.id.img3);
        ImageView img4 = findViewById(R.id.img4);
        ImageView img5 = findViewById(R.id.img5);
        ImageView img6 = findViewById(R.id.img6);

        ImageView[] vistas = {img1, img2, img3, img4, img5, img6};

        for (int i = 0; i < vistas.length; i++) {
            int posicion = i;

            vistas[i].setOnClickListener(v -> {
                Intent intent = new Intent(GaleriaActivity.this, FullscreenActivity.class);
                intent.putExtra("posicion", posicion);
                startActivity(intent);
            });
        }
    }
}
