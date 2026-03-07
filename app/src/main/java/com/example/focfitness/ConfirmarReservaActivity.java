package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfirmarReservaActivity extends AppCompatActivity {

    TextView tvResumenEspacio, tvResumenFecha, tvResumenHora, tvResumenPrecio;
    Button btnConfirmar, btnCancelar;

    String idEspacio, nombreEspacio, fecha, hora;
    double precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_reserva);

        idEspacio = getIntent().getStringExtra("idEspacio");
        nombreEspacio = getIntent().getStringExtra("nombreEspacio");
        fecha = getIntent().getStringExtra("fecha");
        hora = getIntent().getStringExtra("hora");
        precio = getIntent().getDoubleExtra("precioEspacio", 0);

        tvResumenEspacio = findViewById(R.id.tvResumenEspacio);
        tvResumenFecha = findViewById(R.id.tvResumenFecha);
        tvResumenHora = findViewById(R.id.tvResumenHora);
        tvResumenPrecio = findViewById(R.id.tvResumenPrecio);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCancelar = findViewById(R.id.btnCancelar);

        tvResumenEspacio.setText(nombreEspacio);
        tvResumenPrecio.setText(String.format("%.2f€", precio));

        try {
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOut = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
            Date d = sdfIn.parse(fecha);
            String fechaTexto = sdfOut.format(d);
            tvResumenFecha.setText(fechaTexto.substring(0, 1).toUpperCase() + fechaTexto.substring(1));
        } catch (Exception e) {
            tvResumenFecha.setText(fecha);
        }

        String horaFin = String.format("%02d:00", Integer.parseInt(hora.split(":")[0]) + 1);
        tvResumenHora.setText(hora + " - " + horaFin);

        btnConfirmar.setOnClickListener(v -> guardarReserva());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarReserva() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String horaFin = String.format("%02d:00", Integer.parseInt(hora.split(":")[0]) + 1);

        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app").getReference("reservas");
        String idReserva = dbRef.push().getKey();

        Map<String, Object> reserva = new HashMap<>();
        reserva.put("idUsuario", uid);
        reserva.put("idEspacio", idEspacio);
        reserva.put("pueblo", "albolote");
        reserva.put("fecha", fecha);
        reserva.put("horaInicio", hora);
        reserva.put("horaFin", horaFin);
        reserva.put("estado", "confirmada");
        reserva.put("precio", precio);

        dbRef.child(idReserva).setValue(reserva)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "¡Su reserva ha sido confirmada!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmarReservaActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}