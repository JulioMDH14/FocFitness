package com.example.focfitness;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

public class LoginTest {

    private FirebaseAuth mockAuth;
    private Task<AuthResult> mockTask;
    private LoginHelper helper;

    @Before
    public void setUp() {
        mockAuth = mock(FirebaseAuth.class);
        mockTask = mock(Task.class);
        helper = new LoginHelper(mockAuth);
    }

    @Test
    public void validarCampos_camposVacios_retornaFalse() {
        assertFalse(helper.validarCampos("", ""));
    }

    @Test
    public void validarCampos_camposCorrectos_retornaTrue() {
        assertTrue(helper.validarCampos("correo@test.com", "1234"));
    }

    @Test
    public void login_llamaFirebaseAuth() {
        when(mockAuth.signInWithEmailAndPassword("test@test.com", "1234"))
                .thenReturn(mockTask);

        Task<AuthResult> resultado = helper.login("test@test.com", "1234");

        verify(mockAuth).signInWithEmailAndPassword("test@test.com", "1234");
        assertEquals(mockTask, resultado);
    }
}
