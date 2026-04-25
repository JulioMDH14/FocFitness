package com.example.focfitness;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    ImageView imgEditarPerfil;
    EditText etEditarNombre, etEditarApellidos, etEditarCorreo, etEditarTelefono;
    Button btnGuardarPerfil, btnVolverPerfil;

    FirebaseUser user;
    DatabaseReference usuarioRef;
    ActivityResultLauncher<String> seleccionarImagenLauncher;

    String imagenBase64 = "";
    boolean fotoCambiada = false;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        imgEditarPerfil = findViewById(R.id.imgEditarPerfil);
        etEditarNombre = findViewById(R.id.etEditarNombre);
        etEditarApellidos = findViewById(R.id.etEditarApellidos);
        etEditarCorreo = findViewById(R.id.etEditarCorreo);
        etEditarTelefono = findViewById(R.id.etEditarTelefono);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolverPerfil = findViewById(R.id.btnVolverPerfil);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        usuarioRef = FirebaseDatabase.getInstance(DB_URL).getReference("usuarios").child(user.getUid());

        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        prepararImagenSeleccionada(uri);
                    }
                }
        );

        imgEditarPerfil.setOnClickListener(v -> seleccionarImagenLauncher.launch("image/*"));
        btnVolverPerfil.setOnClickListener(v -> finish());
        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());

        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        etEditarCorreo.setText(user.getEmail());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String nombre = obtenerTexto(snapshot, "nombre");
                String apellidos = obtenerTexto(snapshot, "apellidos");
                String nombreCompleto = obtenerTexto(snapshot, "nombreCompleto");
                String correo = obtenerTexto(snapshot, "correo");
                String telefono = obtenerTexto(snapshot, "telefono");
                String imagen = obtenerTexto(snapshot, "imagen");
                imagenBase64 = obtenerTexto(snapshot, "imagenBase64");

                if (nombre.isEmpty() && !nombreCompleto.isEmpty()) {
                    String[] partesNombre = separarNombreYApellidos(nombreCompleto);
                    nombre = partesNombre[0];
                    apellidos = partesNombre[1];
                }

                etEditarNombre.setText(nombre);
                etEditarApellidos.setText(apellidos);
                etEditarCorreo.setText(!correo.isEmpty() ? correo : user.getEmail());
                etEditarTelefono.setText(telefono);
                cargarImagenPerfil(imagenBase64, imagen);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EditarPerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios() {
        String nombre = etEditarNombre.getText().toString().trim();
        String apellidos = etEditarApellidos.getText().toString().trim();
        String correo = etEditarCorreo.getText().toString().trim();
        String telefono = etEditarTelefono.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Rellene nombre, apellidos y telefono", Toast.LENGTH_SHORT).show();
            return;
        }

        setGuardando(true);

        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", nombre);
        cambios.put("apellidos", apellidos);
        cambios.put("nombreCompleto", nombre + " " + apellidos);
        cambios.put("correo", !correo.isEmpty() ? correo : user.getEmail());
        cambios.put("telefono", telefono);
        cambios.put("rol", "cliente");

        if (fotoCambiada) {
            cambios.put("imagen", "");
            cambios.put("imagenBase64", imagenBase64);
        }

        usuarioRef.updateChildren(cambios)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setGuardando(false);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void prepararImagenSeleccionada(Uri uri) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
            Bitmap bitmapOriginal = ImageDecoder.decodeBitmap(source);
            Bitmap bitmapPerfil = redimensionarBitmap(bitmapOriginal, 512);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapPerfil.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

            imagenBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            fotoCambiada = true;
            imgEditarPerfil.setImageBitmap(bitmapPerfil);
        } catch (IOException e) {
            Toast.makeText(this, "No se pudo cargar la foto seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxSize) {
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho <= maxSize && alto <= maxSize) {
            return bitmap;
        }

        float escala = Math.min((float) maxSize / ancho, (float) maxSize / alto);
        int nuevoAncho = Math.round(ancho * escala);
        int nuevoAlto = Math.round(alto * escala);
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true);
    }

    private void cargarImagenPerfil(String imagenBase64, String urlImagen) {
        if (!imagenBase64.isEmpty()) {
            try {
                byte[] bytesImagen = Base64.decode(imagenBase64, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.length);
                imgEditarPerfil.setImageBitmap(bitmap);
                return;
            } catch (IllegalArgumentException e) {
                imgEditarPerfil.setImageResource(R.drawable.ic_user);
                return;
            }
        }

        if (urlImagen.isEmpty()) {
            imgEditarPerfil.setImageResource(R.drawable.ic_user);
            return;
        }

        Picasso.get()
                .load(urlImagen)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(imgEditarPerfil);
    }

    private void setGuardando(boolean guardando) {
        btnGuardarPerfil.setEnabled(!guardando);
        btnVolverPerfil.setEnabled(!guardando);
        imgEditarPerfil.setEnabled(!guardando);
        btnGuardarPerfil.setText(guardando ? "Guardando..." : "Guardar cambios");
    }

    private String obtenerTexto(DataSnapshot snapshot, String campo) {
        String valor = snapshot.child(campo).getValue(String.class);
        return valor != null ? valor.trim() : "";
    }

    private String[] separarNombreYApellidos(String nombreCompleto) {
        String[] palabras = nombreCompleto.trim().split("\\s+", 2);
        String nombre = palabras[0];
        String apellidos = palabras.length > 1 ? palabras[1] : "";
        return new String[]{nombre, apellidos};
    }
}
