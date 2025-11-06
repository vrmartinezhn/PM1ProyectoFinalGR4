package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class VerificarClaveActivity extends AppCompatActivity {

    EditText correoField, claveField;
    Button verificarBtn, regresarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_clave);

        correoField = findViewById(R.id.editTextCorreoVerificacion);
        claveField = findViewById(R.id.editTextClaveVerificacion);
        verificarBtn = findViewById(R.id.buttonVerificarClave);
        regresarBtn = findViewById(R.id.buttonRegresar);

        regresarBtn.setOnClickListener(v -> finish());

        verificarBtn.setOnClickListener(v -> {
            String correo = correoField.getText().toString().trim().toLowerCase();
            String clave = claveField.getText().toString().trim();

            if (TextUtils.isEmpty(correo) || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                correoField.setError("Correo inválido");
                return;
            }

            if (TextUtils.isEmpty(clave)) {
                claveField.setError("Ingresa la clave");
                return;
            }

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("recuperaciones");
            ref.orderByChild("correo").equalTo(correo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean claveValida = false;
                            for (DataSnapshot dato : snapshot.getChildren()) {
                                String claveGuardada = dato.child("clave").getValue(String.class);
                                if (claveGuardada != null && claveGuardada.equals(clave)) {
                                    claveValida = true;
                                    break;
                                }
                            }

                            if (claveValida) {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, clave)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(VerificarClaveActivity.this, CambiarContrasenaActivity.class);
                                                intent.putExtra("correo", correo);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "";
                                                if (errorMsg.toLowerCase().contains("invalid")) {
                                                    Toast.makeText(VerificarClaveActivity.this, "La clave es incorrecta o el usuario no existe", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(VerificarClaveActivity.this, "Error al iniciar sesión: " + errorMsg, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(VerificarClaveActivity.this, "Clave incorrecta. Verifica que sea la última enviada.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(VerificarClaveActivity.this, "Error en Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
