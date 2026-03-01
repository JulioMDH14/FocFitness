package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;

public class RegistrarseActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etTelefono, etContrasena, etConfirmacion;
    Button btnCrearCuenta, btnRetroceder;
    FirebaseAuth auth;
    DatabaseReference dbRef;
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
    auth = FirebaseAuth.getInstance();
    dbRef = FirebaseDatabase.getInstance().getReference("usuarios");

    btnCrearCuenta.setOnClickListener(v -> {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmacion = etConfirmacion.getText().toString().trim();

        if(nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()){
            Toast.makeText(this,"Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmacion)){
            Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", nombre);
                    usuario.put("correo", correo);
                    usuario.put("telefono", telefono);
                    usuario.put("rol", "cliente");

                    dbRef.child(uid).setValue(usuario);

                    Toast.makeText(this, "Su cuenta se ha creado con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrarseActivity.this,InicioSesionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    });

    btnRetroceder.setOnClickListener(v -> {
        finish();
    });
    }}
