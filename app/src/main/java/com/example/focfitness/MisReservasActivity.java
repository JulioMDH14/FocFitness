package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MisReservasActivity extends AppCompatActivity {

    ListView listMisReservas;
    List<Map<String, Object>> listaReservas = new ArrayList<>();
    List<String> listaIds = new ArrayList<>();
    DatabaseReference dbRef;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        listMisReservas = findViewById(R.id.listMisReservas);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("reservas");

        dbRef.orderByChild("idUsuario").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listaReservas.clear();
                        listaIds.clear();

                        List<Map<String, Object>> proximas = new ArrayList<>();
                        List<String> proximasIds = new ArrayList<>();
                        List<Map<String, Object>> pasadas = new ArrayList<>();
                        List<String> pasadasIds = new ArrayList<>();

                        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        for (DataSnapshot reserva : snapshot.getChildren()) {
                            Map<String, Object> datos = (Map<String, Object>) reserva.getValue();
                            String fecha = (String) datos.get("fecha");
                            String estado = (String) datos.get("estado");

                            if ("cancelada".equals(estado) || fecha.compareTo(hoy) < 0) {
                                pasadas.add(datos);
                                pasadasIds.add(reserva.getKey());
                            } else {
                                proximas.add(datos);
                                proximasIds.add(reserva.getKey());
                            }
                        }

                        listaReservas.addAll(proximas);
                        listaIds.addAll(proximasIds);
                        listaReservas.addAll(pasadas);
                        listaIds.addAll(pasadasIds);

                        listMisReservas.setAdapter(new ReservaAdapter(proximas.size()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MisReservasActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ReservaAdapter extends BaseAdapter {

        int numProximas;

        ReservaAdapter(int numProximas) {
            this.numProximas = numProximas;
        }

        @Override
        public int getCount() { return listaReservas.size(); }

        @Override
        public Object getItem(int position) { return listaReservas.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MisReservasActivity.this).inflate(R.layout.item_reserva, parent, false);
            }

            Map<String, Object> reserva = listaReservas.get(position);
            String idReserva = listaIds.get(position);

            TextView tvNombre = convertView.findViewById(R.id.tvNombreReserva);
            TextView tvEstado = convertView.findViewById(R.id.tvEstadoReserva);
            TextView tvFecha = convertView.findViewById(R.id.tvFechaReserva);
            TextView tvHora = convertView.findViewById(R.id.tvHoraReserva);
            TextView tvPueblo = convertView.findViewById(R.id.tvPuebloReserva);
            TextView tvPrecio = convertView.findViewById(R.id.tvPrecioReserva);
            Button btnCancelar = convertView.findViewById(R.id.btnCancelarReserva);
            String idEspacio = (String) reserva.get("idEspacio");
            tvNombre.setText(idEspacio != null ? idEspacio.replace("_", " ") : "");

            FirebaseDatabase.getInstance(DB_URL).getReference("espacios_deportivos/albolote/" + idEspacio + "/nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                tvNombre.setText((String) snapshot.getValue());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });

            try {
                SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdfOut = new SimpleDateFormat("EEE d MMM", new Locale("es", "ES"));
                Date d = sdfIn.parse((String) reserva.get("fecha"));
                tvFecha.setText(sdfOut.format(d));
            } catch (Exception e) {
                tvFecha.setText((String) reserva.get("fecha"));
            }

            tvHora.setText(reserva.get("horaInicio") + " - " + reserva.get("horaFin"));
            tvPueblo.setText((String) reserva.get("pueblo"));

            Object precioObj = reserva.get("precio");
            double precio = 0;
            if (precioObj instanceof Double) precio = (Double) precioObj;
            else if (precioObj instanceof Long) precio = ((Long) precioObj).doubleValue();
            tvPrecio.setText(String.format("%.2f€", precio));

            String estado = (String) reserva.get("estado");
            String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String fechaReserva = (String) reserva.get("fecha");
            boolean esPasada = fechaReserva != null && fechaReserva.compareTo(hoy) < 0;

            if ("cancelada".equals(estado)) {
                tvEstado.setText("Cancelada");
                tvEstado.setTextColor(Color.WHITE);
                tvEstado.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#DC2626"))
                );
            } else if (esPasada) {
                tvEstado.setText("Completada");
                tvEstado.setTextColor(Color.WHITE);
                tvEstado.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#6B7280"))
                );
            } else {
                tvEstado.setText("Confirmada");
                tvEstado.setTextColor(Color.WHITE);
                tvEstado.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#16A34A"))
                );
            }

            if ("cancelada".equals(estado) || esPasada) {
                btnCancelar.setEnabled(false);
                btnCancelar.setAlpha(0.4f);
            } else {
                btnCancelar.setEnabled(true);
                btnCancelar.setAlpha(1f);
                btnCancelar.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MisReservasActivity.this);
                    View view = LayoutInflater.from(MisReservasActivity.this).inflate(R.layout.dialog_cancelar, null);
                    builder.setView(view);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    view.findViewById(R.id.btnConfirmar).setOnClickListener(btn -> {
                        dbRef.child(idReserva).child("estado").setValue("cancelada")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(MisReservasActivity.this, "Su reserva ha sido cancelada", Toast.LENGTH_SHORT).show();
                                    recreate();
                                });
                        dialog.dismiss();
                    });

                    view.findViewById(R.id.btnCancelar).setOnClickListener(btn -> dialog.dismiss());

                    dialog.show();
                });
            }
            return convertView;
        }
    }
}