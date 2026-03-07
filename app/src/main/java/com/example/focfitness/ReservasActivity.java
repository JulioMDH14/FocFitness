package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.database.*;
import java.util.*;

public class ReservasActivity extends AppCompatActivity {

    ListView listEspacios;
    List<Map<String, Object>> listaEspacios = new ArrayList<>();
    List<String> listaIds = new ArrayList<>();
    DatabaseReference dbRef;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        listEspacios = findViewById(R.id.listaEspacios);
        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("espacios_deportivos/albolote");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaEspacios.clear();
                listaIds.clear();

                for (DataSnapshot espacio : snapshot.getChildren()) {
                    Object cuota = espacio.child("cuotaMensual").getValue();
                    if (cuota != null && (Boolean) cuota) continue;

                    Map<String, Object> datos = (Map<String, Object>) espacio.getValue();
                    listaEspacios.add(datos);
                    listaIds.add(espacio.getKey());
                }

                listEspacios.setAdapter(new EspacioAdapter());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ReservasActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listEspacios.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> espacio = listaEspacios.get(position);
            String idEspacio = listaIds.get(position);
            String nombre = (String) espacio.get("nombre");
            double precio = 0;
            Object precioObj = espacio.get("precio");
            if (precioObj instanceof Double) precio = (Double) precioObj;
            else if (precioObj instanceof Long) precio = ((Long) precioObj).doubleValue();

            Intent intent = new Intent(ReservasActivity.this, HorasActivity.class);
            intent.putExtra("idEspacio", idEspacio);
            intent.putExtra("nombreEspacio", nombre);
            intent.putExtra("precioEspacio", precio);
            startActivity(intent);
        });
    }

    class EspacioAdapter extends BaseAdapter {

        @Override
        public int getCount() { return listaEspacios.size(); }

        @Override
        public Object getItem(int position) { return listaEspacios.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ReservasActivity.this)
                        .inflate(R.layout.activity_espacio, parent, false);
            }

            Map<String, Object> espacio = listaEspacios.get(position);

            TextView tvNombre = convertView.findViewById(R.id.tvNombreEspacio);
            TextView tvTipo = convertView.findViewById(R.id.tvTipoEspacio);
            TextView tvPrecio = convertView.findViewById(R.id.tvPrecioEspacio);

            tvNombre.setText((String) espacio.get("nombre"));
            tvTipo.setText((String) espacio.get("tipo"));

            Object precioObj = espacio.get("precio");
            double precio = 0;
            if (precioObj instanceof Double) precio = (Double) precioObj;
            else if (precioObj instanceof Long) precio = ((Long) precioObj).doubleValue();
            tvPrecio.setText(String.format("%.0f€/h", precio));

            return convertView;
        }
    }
}