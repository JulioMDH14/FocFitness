package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class ClasesActivity extends AppCompatActivity {

    ListView listaClases;
    Spinner spinnerDia;
    TextView tvSinClases;

    List<Map<String, Object>> todasClases = new ArrayList<>();
    List<String> todosIds = new ArrayList<>();
    List<Map<String, Object>> clasesFiltradas = new ArrayList<>();
    List<String> idsFiltrados = new ArrayList<>();

    DatabaseReference dbRef;
    String uid;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String[] DIAS = {"Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clases);

        listaClases = findViewById(R.id.listaClases);
        spinnerDia = findViewById(R.id.spinnerDia);
        tvSinClases = findViewById(R.id.tvSinClases);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("clases/albolote");

        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DIAS);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        spinnerDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarPorDia(DIAS[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        cargarClases();
    }

    private void cargarClases() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                todasClases.clear();
                todosIds.clear();
                for (DataSnapshot clase : snapshot.getChildren()) {
                    todasClases.add((Map<String, Object>) clase.getValue());
                    todosIds.add(clase.getKey());
                }
                filtrarPorDia(DIAS[spinnerDia.getSelectedItemPosition()]);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ClasesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarPorDia(String dia) {
        clasesFiltradas.clear();
        idsFiltrados.clear();
        for (int i = 0; i < todasClases.size(); i++) {
            String diaCLase = (String) todasClases.get(i).get("diaSemana");
            if (dia.equals("Todos") || dia.equals(diaCLase)) {
                clasesFiltradas.add(todasClases.get(i));
                idsFiltrados.add(todosIds.get(i));
            }
        }

        if (clasesFiltradas.isEmpty()) {
            tvSinClases.setVisibility(View.VISIBLE);
            listaClases.setVisibility(View.GONE);
        } else {
            tvSinClases.setVisibility(View.GONE);
            listaClases.setVisibility(View.VISIBLE);
        }

        if (listaClases.getAdapter() == null) {
            listaClases.setAdapter(new ClaseAdapter());
        } else {
            ((ClaseAdapter) listaClases.getAdapter()).notifyDataSetChanged();
        }
    }

    class ClaseAdapter extends BaseAdapter {

        @Override
        public int getCount() { return clasesFiltradas.size(); }

        @Override
        public Object getItem(int position) { return clasesFiltradas.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ClasesActivity.this)
                        .inflate(R.layout.item_clase, parent, false);
            }

            Map<String, Object> clase = clasesFiltradas.get(position);
            String idClase = idsFiltrados.get(position);

            TextView tvNombre = convertView.findViewById(R.id.tvNombreClase);
            TextView tvHora = convertView.findViewById(R.id.tvHoraClase);
            TextView tvInstructor = convertView.findViewById(R.id.tvInstructorClase);
            TextView tvPlazas = convertView.findViewById(R.id.tvPlazas);
            Button btnApuntarse = convertView.findViewById(R.id.btnApuntarse);

            tvNombre.setText((String) clase.get("nombre"));
            tvHora.setText(clase.get("diaSemana") + " · " + clase.get("hora"));
            tvInstructor.setText("Instructor: " + clase.get("instructor"));

            long plazasMax = clase.get("plazasMax") instanceof Long ? (Long) clase.get("plazasMax") : 0;
            Map<String, Object> inscritos = clase.get("inscritos") instanceof Map
                    ? (Map<String, Object>) clase.get("inscritos") : new HashMap<>();
            long ocupadas = inscritos.size();
            tvPlazas.setText(ocupadas + "/" + plazasMax + " plazas");

            boolean yaApuntado = inscritos.containsKey(uid);
            boolean lleno = ocupadas >= plazasMax;

            if (yaApuntado) {
                btnApuntarse.setText("✓ Apuntado");
                btnApuntarse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#D1FAE5")));
                btnApuntarse.setTextColor(android.graphics.Color.parseColor("#065F46"));
                btnApuntarse.setOnClickListener(v -> {
                    dbRef.child(idClase).child("inscritos").child(uid).removeValue()
                            .addOnSuccessListener(unused -> cargarClases());
                });
            } else if (lleno) {
                btnApuntarse.setText("Lleno");
                btnApuntarse.setEnabled(false);
                btnApuntarse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#F3F4F6")));
                btnApuntarse.setTextColor(android.graphics.Color.parseColor("#9CA3AF"));
            } else {
                btnApuntarse.setText("Apuntarse");
                btnApuntarse.setEnabled(true);
                btnApuntarse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#DBEAFE")));
                btnApuntarse.setTextColor(android.graphics.Color.parseColor("#1D4ED8"));
                btnApuntarse.setOnClickListener(v -> {
                    dbRef.child(idClase).child("inscritos").child(uid).setValue(true)
                            .addOnSuccessListener(unused -> cargarClases());
                });
            }

            return convertView;
        }
    }
}
