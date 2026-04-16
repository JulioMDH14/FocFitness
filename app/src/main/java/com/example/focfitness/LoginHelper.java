package com.example.focfitness;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginHelper {

    private FirebaseAuth auth;

    public LoginHelper(FirebaseAuth auth) {
        this.auth = auth;
    }

    public boolean validarCampos(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }
}
