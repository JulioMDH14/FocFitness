package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {

    ImageView imgPerfil;
    TextView tvNombrePerfil, tvCorreoPerfil;
    TextView tvDatosNombre, tvDatosApellidos, tvDatosCorreo, tvDatosTelefono;
    Button btnEditarPerfil, btnCerrarSesion;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imgPerfil = findViewById(R.id.imgPerfil);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvDatosNombre = findViewById(R.id.tvDatosNombre);
        tvDatosApellidos = findViewById(R.id.tvDatosApellidos);
        tvDatosCorreo = findViewById(R.id.tvDatosCorreo);
        tvDatosTelefono = findViewById(R.id.tvDatosTelefono);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

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

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }

    private void cargarPerfil() {
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

        FirebaseDatabase.getInstance(DB_URL).getReference("usuarios").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            mostrarPerfilBasico(email);
                            return;
                        }

                        String nombre = obtenerTexto(snapshot, "nombre");
                        String apellidos = obtenerTexto(snapshot, "apellidos");
                        String nombreCompleto = obtenerTexto(snapshot, "nombreCompleto");
                        String correo = obtenerTexto(snapshot, "correo");
                        String telefono = obtenerTexto(snapshot, "telefono");
                        String imagen = obtenerTexto(snapshot, "imagen");
                        String imagenBase64 = obtenerTexto(snapshot, "imagenBase64");

                        if (nombre.isEmpty() && !nombreCompleto.isEmpty()) {
                            String[] partesNombre = separarNombreYApellidos(nombreCompleto);
                            nombre = partesNombre[0];
                            apellidos = partesNombre[1];
                        }

                        if (correo.isEmpty()) {
                            correo = email != null ? email : "-";
                        }

                        String nombreVisible = !nombreCompleto.isEmpty()
                                ? nombreCompleto
                                : (nombre + (!apellidos.isEmpty() ? " " + apellidos : "")).trim();

                        if (nombreVisible.isEmpty()) {
                            nombreVisible = correo.contains("@") ? correo.substring(0, correo.indexOf("@")) : "Usuario";
                        }

                        cargarImagenPerfil(imagenBase64, imagen);
                        tvNombrePerfil.setText(nombreVisible);
                        tvCorreoPerfil.setText(correo);
                        tvDatosNombre.setText(!nombre.isEmpty() ? nombre : nombreVisible);
                        tvDatosApellidos.setText(!apellidos.isEmpty() ? apellidos : "-");
                        tvDatosCorreo.setText(correo);
                        tvDatosTelefono.setText(!telefono.isEmpty() ? telefono : "-");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(PerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarPerfilBasico(String email) {
        String nombreBasico = email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : "Usuario";
        tvNombrePerfil.setText(nombreBasico);
        tvCorreoPerfil.setText(email != null ? email : "-");
        tvDatosNombre.setText(nombreBasico);
        tvDatosApellidos.setText("-");
        tvDatosCorreo.setText(email != null ? email : "-");
        tvDatosTelefono.setText("-");
        imgPerfil.setImageResource(R.drawable.ic_user);
    }

    private String obtenerTexto(DataSnapshot snapshot, String campo) {
        String valor = snapshot.child(campo).getValue(String.class);
        return valor != null ? valor.trim() : "";
    }

    private void cargarImagenPerfil(String imagenBase64, String urlImagen) {
        if (!imagenBase64.isEmpty()) {
            try {
                byte[] bytesImagen = Base64.decode(imagenBase64, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.length);
                imgPerfil.setImageBitmap(bitmap);
                return;
            } catch (IllegalArgumentException e) {
                imgPerfil.setImageResource(R.drawable.ic_user);
                return;
            }
        }

        if (urlImagen.isEmpty()) {
            imgPerfil.setImageResource(R.drawable.ic_user);
            return;
        }

        Picasso.get()
                .load(urlImagen)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(imgPerfil);
    }

    private String[] separarNombreYApellidos(String nombreCompleto) {
        String[] palabras = nombreCompleto.trim().split("\\s+", 2);
        String nombre = palabras[0];
        String apellidos = palabras.length > 1 ? palabras[1] : "";
        return new String[]{nombre, apellidos};
    }
}
