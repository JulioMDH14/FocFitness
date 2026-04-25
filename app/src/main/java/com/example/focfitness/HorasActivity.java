package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HorasActivity extends AppCompatActivity {

    TextView tvNombre, tvPrecio, tvFechaSeleccionada;
    LinearLayout layoutDias;
    ListView listHoras;
    Button btnContinuar;

    String idEspacio, nombreEspacio;
    double precioEspacio;
    String fechaSeleccionada = "";
    String horaSeleccionada = "";

    List<String> horasDisponibles = new ArrayList<>();
    Set<String> horasOcupadas = new HashSet<>();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas);

        idEspacio = getIntent().getStringExtra("idEspacio");
        nombreEspacio = getIntent().getStringExtra("nombreEspacio");
        precioEspacio = getIntent().getDoubleExtra("precioEspacio", 0);

        tvNombre = findViewById(R.id.tvNombreEspacioHoras);
        tvPrecio = findViewById(R.id.tvPrecioEspacioHoras);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        layoutDias = findViewById(R.id.layoutDias);
        listHoras = findViewById(R.id.listHoras);
        btnContinuar = findViewById(R.id.btnContinuar);

        tvNombre.setText(nombreEspacio);
        tvPrecio.setText(String.format("%.0f€/hora", precioEspacio));

        dbRef = FirebaseDatabase.getInstance("https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app").getReference("reservas");

        generarDias();

        btnContinuar.setOnClickListener(v -> {
            if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Selecciona un día y una hora", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(HorasActivity.this, ConfirmarReservaActivity.class);
            intent.putExtra("idEspacio", idEspacio);
            intent.putExtra("nombreEspacio", nombreEspacio);
            intent.putExtra("precioEspacio", precioEspacio);
            intent.putExtra("fecha", fechaSeleccionada);
            intent.putExtra("hora", horaSeleccionada);
            startActivity(intent);
        });
    }

    private void generarDias() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfMostrar = new SimpleDateFormat("EEE\ndd", new Locale("es", "ES"));

        for (int i = 0; i < 7; i++) {
            String fechaKey = sdfKey.format(cal.getTime());
            String fechaMostrar = sdfMostrar.format(cal.getTime()).toUpperCase();

            LinearLayout btnDia = new LinearLayout(this);
            btnDia.setOrientation(LinearLayout.VERTICAL);
            btnDia.setGravity(android.view.Gravity.CENTER);
            btnDia.setPadding(24, 16, 24, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(6, 0, 6, 0);
            btnDia.setLayoutParams(params);
            btnDia.setBackgroundResource(R.drawable.bg_crear);
            btnDia.setClickable(true);
            btnDia.setFocusable(true);

            String[] partes = fechaMostrar.split("\n");
            TextView tvLetra = new TextView(this);
            tvLetra.setText(partes[0]);
            tvLetra.setTextSize(11);
            tvLetra.setTextColor(Color.parseColor("#6B7280"));
            tvLetra.setGravity(android.view.Gravity.CENTER);

            TextView tvNum = new TextView(this);
            tvNum.setText(partes[1]);
            tvNum.setTextSize(18);
            tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
            tvNum.setTextColor(Color.parseColor("#111827"));
            tvNum.setGravity(android.view.Gravity.CENTER);

            btnDia.addView(tvLetra);
            btnDia.addView(tvNum);
            layoutDias.addView(btnDia);

            btnDia.setTag(fechaKey);
            btnDia.setOnClickListener(v -> {
                for (int j = 0; j < layoutDias.getChildCount(); j++) {
                    View child = layoutDias.getChildAt(j);
                    child.setBackgroundResource(R.drawable.bg_crear);
                    ((TextView) ((LinearLayout) child).getChildAt(0)).setTextColor(Color.parseColor("#6B7280"));
                    ((TextView) ((LinearLayout) child).getChildAt(1)).setTextColor(Color.parseColor("#111827"));
                }

                btnDia.setBackgroundResource(R.drawable.bg_iniciar);
                tvLetra.setTextColor(Color.WHITE);
                tvNum.setTextColor(Color.WHITE);

                fechaSeleccionada = (String) btnDia.getTag();
                horaSeleccionada = "";
                cargarHoras(fechaSeleccionada);
            });

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void cargarHoras(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d = sdf.parse(fecha);
            SimpleDateFormat sdfMostrar = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
            tvFechaSeleccionada.setText("Horas disponibles — " + sdfMostrar.format(d).substring(0, 1).toUpperCase()
                    + sdfMostrar.format(d).substring(1));
        } catch (Exception e) { tvFechaSeleccionada.setText("Horas disponibles"); }

        horasDisponibles.clear();
        for (int h = 9; h < 21; h++) {
            horasDisponibles.add(String.format("%02d:00", h));
        }

        horasOcupadas.clear();
        dbRef.orderByChild("fecha").equalTo(fecha)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot reserva : snapshot.getChildren()) {
                            String espacio = (String) reserva.child("idEspacio").getValue();
                            String hora = (String) reserva.child("horaInicio").getValue();
                            String estado = (String) reserva.child("estado").getValue();
                            if (idEspacio.equals(espacio) && !"cancelada".equals(estado)) {
                                horasOcupadas.add(hora);
                            }
                        }
                        listHoras.setAdapter(new HoraAdapter());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    class HoraAdapter extends BaseAdapter {

        @Override
        public int getCount() { return horasDisponibles.size(); }

        @Override
        public Object getItem(int position) { return horasDisponibles.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(HorasActivity.this)
                        .inflate(R.layout.item_hora, parent, false);
            }

            String hora = horasDisponibles.get(position);
            String horaFin = String.format("%02d:00", Integer.parseInt(hora.split(":")[0]) + 1);
            boolean ocupada = horasOcupadas.contains(hora);
            boolean seleccionada = hora.equals(horaSeleccionada);

            TextView tvHora = convertView.findViewById(R.id.tvHora);
            TextView tvEstado = convertView.findViewById(R.id.tvEstado);

            tvHora.setText(hora + " - " + horaFin);

            if (ocupada) {
                tvHora.setTextColor(Color.parseColor("#9CA3AF"));
                tvEstado.setText("Ocupada");
                tvEstado.setTextColor(Color.parseColor("#9CA3AF"));
                convertView.setAlpha(0.5f);
                convertView.setClickable(false);
            } else if (seleccionada) {
                tvHora.setTextColor(Color.WHITE);
                tvEstado.setText("Seleccionada");
                tvEstado.setTextColor(Color.WHITE);
                convertView.setAlpha(1f);
                convertView.setBackgroundColor(Color.parseColor("#1D4ED8"));
                convertView.setClickable(true);
            } else {
                tvHora.setTextColor(Color.parseColor("#1D4ED8"));
                tvEstado.setText("Disponible");
                tvEstado.setTextColor(Color.parseColor("#1D4ED8"));
                convertView.setAlpha(1f);
                convertView.setBackgroundColor(Color.WHITE);
                convertView.setClickable(true);
            }

            convertView.setOnClickListener(v -> {
                if (!ocupada) {
                    horaSeleccionada = hora;
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}