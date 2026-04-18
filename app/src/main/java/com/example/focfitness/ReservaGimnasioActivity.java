package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.database.*;

public class ReservaGimnasioActivity extends AppCompatActivity {

    TextView tvNombreGim;
    Button btnElegirHora;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_gimnasio);

        tvNombreGim = findViewById(R.id.tvNombreGim);
        btnElegirHora = findViewById(R.id.btnElegirHora);

        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("espacios_deportivos/albolote/gimnasio");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String nombre = (String) snapshot.child("nombre").getValue();
                String nombreFinal = nombre != null ? nombre : "Gimnasio";
                tvNombreGim.setText(nombreFinal);

                btnElegirHora.setOnClickListener(v -> {
                    Intent intent = new Intent(ReservaGimnasioActivity.this, HorasActivity.class);
                    intent.putExtra("idEspacio", "gimnasio");
                    intent.putExtra("nombreEspacio", nombreFinal);
                    intent.putExtra("precioEspacio", 0.0);
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
