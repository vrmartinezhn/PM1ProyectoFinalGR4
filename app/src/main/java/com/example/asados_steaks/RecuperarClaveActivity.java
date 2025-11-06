package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RecuperarClaveActivity extends AppCompatActivity {

    EditText emailField;
    Button enviarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);

        emailField = findViewById(R.id.editTextCorreo);
        enviarBtn = findViewById(R.id.buttonEnviarClave);

        enviarBtn.setOnClickListener(v -> {
            String correo = emailField.getText().toString().trim();

            if (TextUtils.isEmpty(correo)) {
                emailField.setError("Este campo es obligatorio");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                emailField.setError("Correo inválido");
                return;
            }

            String claveTemporal = generarClaveTemporal();

            // Guardar en Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("recuperaciones");
            HashMap<String, Object> datos = new HashMap<>();
            datos.put("correo", correo);
            datos.put("clave", claveTemporal);
            ref.push().setValue(datos);

            // Enviar al servidor PHP
            new Thread(() -> {
                try {
                    URL url = new URL("http://10.0.2.2/asados/enviar_clave.php?correo=" + correo + "&clave=" + claveTemporal);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.getInputStream(); // Ejecuta la petición

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Clave enviada al correo", Toast.LENGTH_LONG).show();

                        // Redirigir a la pantalla de verificación
                        Intent intent = new Intent(RecuperarClaveActivity.this, VerificarClaveActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error al enviar correo", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    public String generarClaveTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * caracteres.length());
            clave.append(caracteres.charAt(index));
        }
        return clave.toString();
    }
}
