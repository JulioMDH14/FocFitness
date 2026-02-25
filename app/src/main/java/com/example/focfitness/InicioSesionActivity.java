package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class InicioSesionActivity extends AppCompatActivity {
    Button btnLogin, btnRegistro;
    EditText etCorreo, etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);

        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if(correo.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this,"Rellena todos los campos para continuar",Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(InicioSesionActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivity.this,RegistrarseActivity.class);
            startActivity(intent);
        });
    }
}
