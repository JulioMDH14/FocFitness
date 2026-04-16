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
    Spinner spinnerFiltro;

    List<Map<String, Object>> todosEspacios = new ArrayList<>();
    List<String> todosIds = new ArrayList<>();
    List<Map<String, Object>> listaEspacios = new ArrayList<>();
    List<String> listaIds = new ArrayList<>();

    DatabaseReference dbRef;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        listEspacios = findViewById(R.id.listaEspacios);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("espacios_deportivos/albolote");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                todosEspacios.clear();
                todosIds.clear();

                for (DataSnapshot espacio : snapshot.getChildren()) {
                    Object cuota = espacio.child("cuotaMensual").getValue();
                    if (cuota != null && (Boolean) cuota) continue;

                    Map<String, Object> datos = (Map<String, Object>) espacio.getValue();
                    todosEspacios.add(datos);
                    todosIds.add(espacio.getKey());
                }

                configurarFiltro();
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

    private static final Map<String, String> NOMBRES_TIPO = new HashMap<String, String>() {{
        put("campo",    "Campo");
        put("pabellon", "Pabellón");
        put("piscina",  "Piscina");
        put("pista",    "Pista");
        put("gimnasio", "Gimnasio");
    }};

    private String nombreVisible(String tipo) {
        String nombre = NOMBRES_TIPO.get(tipo);
        return nombre != null ? nombre : tipo;
    }

    private void configurarFiltro() {
        List<String> tiposFirebase = new ArrayList<>();
        List<String> tiposVisibles = new ArrayList<>();
        tiposFirebase.add("Todos");
        tiposVisibles.add("Todos");

        for (Map<String, Object> e : todosEspacios) {
            String tipo = (String) e.get("tipo");
            if (tipo != null && !tiposFirebase.contains(tipo)) {
                tiposFirebase.add(tipo);
                tiposVisibles.add(nombreVisible(tipo));
            }
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposVisibles);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterSpinner);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro(tiposFirebase.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        aplicarFiltro("Todos");
    }

    private void aplicarFiltro(String tipo) {
        listaEspacios.clear();
        listaIds.clear();

        for (int i = 0; i < todosEspacios.size(); i++) {
            Map<String, Object> e = todosEspacios.get(i);
            if (tipo.equals("Todos") || tipo.equals(e.get("tipo"))) {
                listaEspacios.add(e);
                listaIds.add(todosIds.get(i));
            }
        }

        if (listEspacios.getAdapter() == null) {
            listEspacios.setAdapter(new EspacioAdapter());
        } else {
            ((EspacioAdapter) listEspacios.getAdapter()).notifyDataSetChanged();
        }
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