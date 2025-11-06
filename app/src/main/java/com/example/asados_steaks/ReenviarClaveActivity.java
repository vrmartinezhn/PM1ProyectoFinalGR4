package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class ReenviarClaveActivity extends AppCompatActivity {

    EditText correoInput;
    Button reenviarBtn, regresarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reenviar_clave);

        correoInput = findViewById(R.id.editTextCorreoReenvio);
        reenviarBtn = findViewById(R.id.buttonReenviarClave);
        regresarBtn = findViewById(R.id.buttonRegresar);

        reenviarBtn.setOnClickListener(v -> {
            String correo = correoInput.getText().toString().trim().toLowerCase();
            if (correo.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            String nuevaClave = generarClaveTemporal();
            enviarClavePorCorreo(correo, nuevaClave);
        });

        regresarBtn.setOnClickListener(v -> finish());
    }

    private String generarClaveTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder clave = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            clave.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return clave.toString();
    }

    private void enviarClavePorCorreo(String correo, String claveTemporal) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/asados/enviar_clave.php?correo=" + correo + "&clave=" + claveTemporal);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("GET");
                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(5000);

                int responseCode = conexion.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    respuesta.append(inputLine);
                }
                in.close();

                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK && respuesta.toString().contains("Correo enviado")) {
                        Toast.makeText(this, "Clave enviada al correo", Toast.LENGTH_LONG).show();

                        // ✅ Guardar en Firebase Realtime Database
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("recuperaciones");
                        HashMap<String, Object> datos = new HashMap<>();
                        datos.put("correo", correo);
                        datos.put("clave", claveTemporal);
                        ref.push().setValue(datos);

                        // ✅ Crear o actualizar usuario en Firebase Authentication
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.fetchSignInMethodsForEmail(correo)
                                .addOnCompleteListener(checkTask -> {
                                    if (checkTask.isSuccessful()) {
                                        boolean existe = !checkTask.getResult().getSignInMethods().isEmpty();

                                        if (existe) {
                                            auth.signInWithEmailAndPassword(correo, claveTemporal)
                                                    .addOnCompleteListener(loginTask -> {
                                                        if (loginTask.isSuccessful()) {
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            if (user != null) {
                                                                user.updatePassword(claveTemporal);
                                                                guardarRolUsuario(user, correo);
                                                                enviarCorreoVerificacion(user);
                                                            }
                                                        } else {
                                                            Toast.makeText(this, "No se pudo actualizar la clave temporal", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            auth.createUserWithEmailAndPassword(correo, claveTemporal)
                                                    .addOnCompleteListener(createTask -> {
                                                        if (createTask.isSuccessful()) {
                                                            FirebaseUser user = auth.getCurrentUser();
                                                            if (user != null) {
                                                                guardarRolUsuario(user, correo);
                                                                enviarCorreoVerificacion(user);
                                                            }
                                                        } else {
                                                            Toast.makeText(this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                });

                        // ✅ Redirigir a verificación
                        Intent intent = new Intent(ReenviarClaveActivity.this, VerificarClaveActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Servidor respondió con error: " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void guardarRolUsuario(FirebaseUser user, String correo) {
        String uid = user.getUid();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        HashMap<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("correo", correo);
        datosUsuario.put("rol", "usuario");
        usuariosRef.child(uid).setValue(datosUsuario);
    }

    private void enviarCorreoVerificacion(FirebaseUser user) {
        if (!user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Correo de verificación enviado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error al enviar verificación: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
