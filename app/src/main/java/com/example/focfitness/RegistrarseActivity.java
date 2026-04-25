package com.example.focfitness;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class RegistrarseActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etTelefono, etContrasena, etConfirmacion;
    Button btnCrearCuenta, btnRetroceder;
    ImageView imgPerfil;

    FirebaseAuth auth;
    DatabaseReference dbRef;

    Uri imagenUri;
    String imagenBase64 = "";
    ActivityResultLauncher<String> seleccionarImagenLauncher;

    private static final String DB_URL = "https://focfitness-55cab-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmacion = findViewById(R.id.etConfirmacion);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnRetroceder = findViewById(R.id.btnRetroceder);

        imgPerfil = findViewById(R.id.imgPerfil);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("usuarios");

        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        prepararImagenSeleccionada(uri);
                    }
                }
        );

        imgPerfil.setOnClickListener(v -> seleccionarImagenLauncher.launch("image/*"));

        btnCrearCuenta.setOnClickListener(v -> {
            String nombreCompleto = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String confirmacion = etConfirmacion.getText().toString().trim();

            if(nombreCompleto.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()){
                Toast.makeText(this,"Rellene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(confirmacion)){
                Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            setRegistroEnProgreso(true);

            auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnSuccessListener(result -> {
                        FirebaseUser usuarioAuth = result.getUser();
                        if (usuarioAuth == null) {
                            setRegistroEnProgreso(false);
                            Toast.makeText(this, "No se pudo obtener el usuario creado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        guardarUsuario(usuarioAuth.getUid(), nombreCompleto, correo, telefono);
                    })

                    .addOnFailureListener(e -> {
                        setRegistroEnProgreso(false);
                        Toast.makeText(this, obtenerMensajeErrorRegistro(e), Toast.LENGTH_LONG).show();
                    });
        });

        btnRetroceder.setOnClickListener(v -> finish());
    }

    private void prepararImagenSeleccionada(Uri uri) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
            Bitmap bitmapOriginal = ImageDecoder.decodeBitmap(source);
            Bitmap bitmapPerfil = redimensionarBitmap(bitmapOriginal, 512);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapPerfil.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

            imagenUri = uri;
            imagenBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            imgPerfil.setImageBitmap(bitmapPerfil);
        } catch (IOException e) {
            imagenUri = null;
            imagenBase64 = "";
            imgPerfil.setImageResource(R.drawable.ic_user);
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

    private void guardarUsuario(String uid, String nombreCompleto, String correo, String telefono) {

        String[] partesNombre = separarNombreYApellidos(nombreCompleto);

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", partesNombre[0]);
        usuario.put("apellidos", partesNombre[1]);
        usuario.put("nombreCompleto", nombreCompleto);
        usuario.put("correo", correo);
        usuario.put("telefono", telefono);
        usuario.put("rol", "cliente");
        usuario.put("imagen", "");
        usuario.put("imagenBase64", imagenBase64);

        dbRef.child(uid).setValue(usuario)
                .addOnSuccessListener(unused -> finalizarRegistro("Su cuenta se ha creado con exito"))
                .addOnFailureListener(e -> {
                    setRegistroEnProgreso(false);
                    Toast.makeText(this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void finalizarRegistro(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegistrarseActivity.this, InicioSesionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setRegistroEnProgreso(boolean enProgreso) {
        btnCrearCuenta.setEnabled(!enProgreso);
        btnRetroceder.setEnabled(!enProgreso);
        imgPerfil.setEnabled(!enProgreso);
        btnCrearCuenta.setText(enProgreso ? "Creando cuenta..." : "Crear Cuenta");
    }

    private String obtenerMensajeErrorRegistro(Exception error) {
        if (error instanceof FirebaseAuthUserCollisionException) {
            return "Ese correo ya esta registrado. Inicia sesion o usa otro correo.";
        }

        if (error instanceof FirebaseAuthWeakPasswordException) {
            return "La contrasena debe tener al menos 6 caracteres.";
        }

        if (error instanceof FirebaseAuthInvalidCredentialsException) {
            return "El correo electronico no tiene un formato valido.";
        }

        return "No se pudo crear la cuenta: " + error.getMessage();
    }

    private String[] separarNombreYApellidos(String nombreCompleto) {
        String[] palabras = nombreCompleto.trim().split("\\s+", 2);
        String nombre = palabras[0];
        String apellidos = palabras.length > 1 ? palabras[1] : "";
        return new String[]{nombre, apellidos};
    }
}
