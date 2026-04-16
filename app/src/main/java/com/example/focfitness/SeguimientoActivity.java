package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SeguimientoActivity extends AppCompatActivity {

    ListView listaEjercicios;
    EditText etNombre, etPeso, etSeries;
    Button btnAnadir, btnDiaAnterior, btnDiaSiguiente;
    TextView tvFechaHoy, tvVacio, tvClasesHoy;
    LinearLayout seccionClases;

    List<Map<String, Object>> lista = new ArrayList<>();
    List<String> listaIds = new ArrayList<>();

    DatabaseReference dbBase;
    DatabaseReference dbRef;
    Calendar calendario;
    String uid;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String[] DIAS_ES = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento);

        listaEjercicios = findViewById(R.id.listaEjercicios);
        etNombre = findViewById(R.id.etNombre);
        etPeso = findViewById(R.id.etPeso);
        etSeries = findViewById(R.id.etSeries);
        btnAnadir = findViewById(R.id.btnAnadir);
        tvFechaHoy = findViewById(R.id.tvFechaHoy);
        btnDiaAnterior = findViewById(R.id.btnDiaAnterior);
        btnDiaSiguiente = findViewById(R.id.btnDiaSiguiente);
        tvVacio = findViewById(R.id.tvVacio);
        tvClasesHoy = findViewById(R.id.tvClasesHoy);
        seccionClases = findViewById(R.id.seccionClases);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbBase = FirebaseDatabase.getInstance(DB_URL).getReference("seguimiento").child(uid);

        calendario = Calendar.getInstance();
        actualizarDia();

        btnDiaAnterior.setOnClickListener(v -> {
            calendario.add(Calendar.DAY_OF_YEAR, -1);
            actualizarDia();
        });

        btnDiaSiguiente.setOnClickListener(v -> {
            calendario.add(Calendar.DAY_OF_YEAR, 1);
            actualizarDia();
        });

        btnAnadir.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String peso = etPeso.getText().toString().trim();
            String series = etSeries.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("Escribe el nombre del ejercicio");
                return;
            }

            Map<String, Object> ejercicio = new HashMap<>();
            ejercicio.put("nombre", nombre);
            ejercicio.put("peso", peso.isEmpty() ? "-" : peso);
            ejercicio.put("series", series.isEmpty() ? "-" : series);

            dbRef.push().setValue(ejercicio).addOnSuccessListener(unused -> {
                etNombre.setText("");
                etPeso.setText("");
                etSeries.setText("");
                cargarEjercicios();
            });
        });
    }

    private void actualizarDia() {
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendario.getTime());
        SimpleDateFormat sdfVisible = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        tvFechaHoy.setText(sdfVisible.format(calendario.getTime()));
        dbRef = dbBase.child(fecha);
        cargarEjercicios();
        cargarClasesDelDia();
    }

    private void cargarClasesDelDia() {
        String diaHoy = DIAS_ES[calendario.get(Calendar.DAY_OF_WEEK) - 1];
        FirebaseDatabase.getInstance(DB_URL).getReference("clases/albolote")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        StringBuilder sb = new StringBuilder();
                        for (DataSnapshot clase : snapshot.getChildren()) {
                            String dia = (String) clase.child("diaSemana").getValue();
                            Object inscrObj = clase.child("inscritos").child(uid).getValue();
                            if (diaHoy.equals(dia) && inscrObj != null) {
                                String nombre = (String) clase.child("nombre").getValue();
                                String hora = (String) clase.child("hora").getValue();
                                if (sb.length() > 0) sb.append("\n");
                                sb.append("• ").append(nombre).append(" — ").append(hora);
                            }
                        }
                        if (sb.length() > 0) {
                            tvClasesHoy.setText(sb.toString());
                            seccionClases.setVisibility(View.VISIBLE);
                        } else {
                            seccionClases.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void cargarEjercicios() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lista.clear();
                listaIds.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    lista.add((Map<String, Object>) item.getValue());
                    listaIds.add(item.getKey());
                }
                if (lista.isEmpty()) {
                    tvVacio.setVisibility(android.view.View.VISIBLE);
                    listaEjercicios.setVisibility(android.view.View.GONE);
                } else {
                    tvVacio.setVisibility(android.view.View.GONE);
                    listaEjercicios.setVisibility(android.view.View.VISIBLE);
                }
                if (listaEjercicios.getAdapter() == null) {
                    listaEjercicios.setAdapter(new EjercicioAdapter());
                } else {
                    ((EjercicioAdapter) listaEjercicios.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SeguimientoActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class EjercicioAdapter extends BaseAdapter {

        @Override
        public int getCount() { return lista.size(); }

        @Override
        public Object getItem(int position) { return lista.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SeguimientoActivity.this)
                        .inflate(R.layout.item_ejercicio, parent, false);
            }

            Map<String, Object> ejercicio = lista.get(position);
            String id = listaIds.get(position);

            TextView tvNombre = convertView.findViewById(R.id.tvNombreEjercicio);
            TextView tvPeso = convertView.findViewById(R.id.tvPeso);
            TextView tvSeries = convertView.findViewById(R.id.tvSeries);
            Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

            tvNombre.setText((String) ejercicio.get("nombre"));
            tvPeso.setText(ejercicio.get("peso") + " kg");
            tvSeries.setText((String) ejercicio.get("series"));

            btnEliminar.setOnClickListener(v -> {
                dbRef.child(id).removeValue().addOnSuccessListener(unused -> cargarEjercicios());
            });

            return convertView;
        }
    }
}
