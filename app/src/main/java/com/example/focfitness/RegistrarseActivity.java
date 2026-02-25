package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
public class RegistrarseActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etTelefono, etContrasena, etConfirmacion;
    Button btnCrearCuenta, btnRetroceder;
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

    btnCrearCuenta.setOnClickListener(v -> {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmacion = etConfirmacion.getText().toString().trim();

        if(nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()){
            Toast.makeText(this,"Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }

        //TODO-01 Crear expresiones regulares para el correo y la contraseña

        if (!contrasena.equals(confirmacion)){
            Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }

        //TODO-02 Registrar usuario en la base de datos (Crear base de datos y conexión)
    });

    btnRetroceder.setOnClickListener(v -> {
        finish();
    });
    }}
