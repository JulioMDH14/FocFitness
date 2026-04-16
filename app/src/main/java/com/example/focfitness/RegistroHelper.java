package com.example.focfitness;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RegistroHelper {

    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    public RegistroHelper(FirebaseAuth auth, DatabaseReference dbRef) {
        this.auth = auth;
        this.dbRef = dbRef;
    }

    public boolean validarCampos(String nombre, String correo, String telefono, String pass, String confirm) {
        return !nombre.isEmpty() && !correo.isEmpty() && !telefono.isEmpty()
                && !pass.isEmpty() && !confirm.isEmpty();
    }

    public boolean contrasenasCoinciden(String pass, String confirm) {
        return pass.equals(confirm);
    }

    public Task<AuthResult> registrar(String correo, String pass) {
        return auth.createUserWithEmailAndPassword(correo, pass);
    }

    public Map<String, Object> crearMapaUsuario(String nombre, String correo, String telefono) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("correo", correo);
        usuario.put("telefono", telefono);
        usuario.put("rol", "cliente");
        return usuario;
    }
}
