package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(PerfilActivity.this, InicioSesionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        tvCorreoPerfil.setText(email);
        tvDatosCorreo.setText(email);

        FirebaseDatabase.getInstance(DB_URL).getReference("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            mostrarPerfilBasico(email);
                            Toast.makeText(PerfilActivity.this, "No hay datos de perfil guardados para este usuario", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String nombre = (String) snapshot.child("nombre").getValue();
                        String apellidos = (String) snapshot.child("apellidos").getValue();
                        String telefono = (String) snapshot.child("telefono").getValue();

                        if (nombre != null && !nombre.isEmpty()) {
                            tvNombrePerfil.setText(nombre + (apellidos != null && !apellidos.isEmpty() ? " " + apellidos : ""));
                            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
                            tvDatosNombre.setText(nombre);
                        } else {
                            mostrarPerfilBasico(email);
                        }

                        tvDatosApellidos.setText(apellidos != null && !apellidos.isEmpty() ? apellidos : "-");
                        tvDatosTelefono.setText(telefono != null && !telefono.isEmpty() ? telefono : "-");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(PerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                });

        //TODO-05 Crear Activity para editar los datos del perfil
        /*.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            startActivity(intent);
        });*/

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PerfilActivity.this, InicioSesionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarPerfilBasico(String email) {
        String nombreBasico = email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : "Usuario";
        tvNombrePerfil.setText(nombreBasico);
        tvAvatar.setText(String.valueOf(nombreBasico.charAt(0)).toUpperCase());
        tvDatosNombre.setText(nombreBasico);
        tvDatosApellidos.setText("-");
        tvDatosTelefono.setText("-");
    }
}
