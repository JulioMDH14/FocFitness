package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.database.*;

public class ReservaGimnasioActivity extends AppCompatActivity {

    TextView tvNombreGim, tvPrecioGim;
    Button btnElegirHora;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_gimnasio);

        tvNombreGim = findViewById(R.id.tvNombreGim);
        tvPrecioGim = findViewById(R.id.tvPrecioGim);
        btnElegirHora = findViewById(R.id.btnElegirHora);

        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("espacios_deportivos/albolote/gimnasio");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String nombre = (String) snapshot.child("nombre").getValue();
                Object precioObj = snapshot.child("precio").getValue();
                double precio = 0;
                if (precioObj instanceof Double) precio = (Double) precioObj;
                else if (precioObj instanceof Long) precio = ((Long) precioObj).doubleValue();

                String nombreFinal = nombre != null ? nombre : "Gimnasio";
                tvNombreGim.setText(nombreFinal);
                tvPrecioGim.setText(String.format("%.0f€/h", precio));

                double precioFinal = precio;
                btnElegirHora.setOnClickListener(v -> {
                    Intent intent = new Intent(ReservaGimnasioActivity.this, HorasActivity.class);
                    intent.putExtra("idEspacio", "gimnasio");
                    intent.putExtra("nombreEspacio", nombreFinal);
                    intent.putExtra("precioEspacio", precioFinal);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ReservaGimnasioActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
