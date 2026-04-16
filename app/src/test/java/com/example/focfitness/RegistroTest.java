package com.example.focfitness;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class RegistroTest {

    private FirebaseAuth mockAuth;
    private DatabaseReference mockDb;
    private Task<AuthResult> mockTask;
    private RegistroHelper helper;

    @Before
    public void setUp() {
        mockAuth = mock(FirebaseAuth.class);
        mockDb = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        helper = new RegistroHelper(mockAuth, mockDb);
    }

    @Test
    public void validarCampos_camposVacios_retornaFalse() {
        assertFalse(helper.validarCampos("", "", "", "", ""));
    }

    @Test
    public void validarCampos_camposCorrectos_retornaTrue() {
        assertTrue(helper.validarCampos("Julio", "correo@test.com", "600000000", "1234", "1234"));
    }

    @Test
    public void contrasenasCoinciden_correctas_retornaTrue() {
        assertTrue(helper.contrasenasCoinciden("1234", "1234"));
    }

    @Test
    public void contrasenasCoinciden_incorrectas_retornaFalse() {
        assertFalse(helper.contrasenasCoinciden("1234", "abcd"));
    }

    @Test
    public void registrar_llamaFirebaseAuth() {
        when(mockAuth.createUserWithEmailAndPassword("test@test.com", "1234"))
                .thenReturn(mockTask);

        Task<AuthResult> resultado = helper.registrar("test@test.com", "1234");

        verify(mockAuth).createUserWithEmailAndPassword("test@test.com", "1234");
        assertEquals(mockTask, resultado);
    }

    @Test
    public void crearMapaUsuario_devuelveDatosCorrectos() {
        Map<String, Object> usuario = helper.crearMapaUsuario("Julio", "correo@test.com", "600000000");

        assertEquals("Julio", usuario.get("nombre"));
        assertEquals("correo@test.com", usuario.get("correo"));
        assertEquals("600000000", usuario.get("telefono"));
        assertEquals("cliente", usuario.get("rol"));
    }
}
