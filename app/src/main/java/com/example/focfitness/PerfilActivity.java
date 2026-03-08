package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class PerfilActivity extends AppCompatActivity {

    TextView tvAvatar, tvNombrePerfil, tvCorreoPerfil;
    TextView tvDatosNombre, tvDatosApellidos, tvDatosCorreo, tvDatosTelefono;
    Button btnEditarPerfil, btnCerrarSesion;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvAvatar = findViewById(R.id.tvAvatar);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvDatosNombre = findViewById(R.id.tvDatosNombre);
        tvDatosApellidos = findViewById(R.id.tvDatosApellidos);
        tvDatosCorreo = findViewById(R.id.tvDatosCorreo);
        tvDatosTelefono = findViewById(R.id.tvDatosTelefono);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        tvCorreoPerfil.setText(email);
        tvDatosCorreo.setText(email);

        FirebaseDatabase.getInstance(DB_URL).getReference("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String nombre = (String) snapshot.child("nombre").getValue();
                        String apellidos = (String) snapshot.child("apellidos").getValue();
                        String telefono = (String) snapshot.child("telefono").getValue();

                        if (nombre != null) {
                            tvNombrePerfil.setText(nombre + (apellidos != null ? " " + apellidos : ""));
                            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
                            tvDatosNombre.setText(nombre);
                        }

                        tvDatosApellidos.setText(apellidos != null ? apellidos : "—");
                        tvDatosTelefono.setText(telefono != null ? telefono : "—");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(PerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                });

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PerfilActivity.this, InicioSesionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}