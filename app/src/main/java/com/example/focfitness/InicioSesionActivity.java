package com.example.focfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioSesionActivity extends AppCompatActivity {
    Button btnLogin, btnRegistro;
    EditText etCorreo, etContrasena;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null){
            Intent intent = new Intent(InicioSesionActivity.this, HomeActivity.class);
            startActivity(intent);
        }

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

            auth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnSuccessListener(result -> {
                        Intent intent = new Intent(InicioSesionActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "El correo o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                    });
        });

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivity.this,RegistrarseActivity.class);
            startActivity(intent);
        });
    }
}
