package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText emailField, passwordField, nombreField;
    Button registerBtn, backToLoginBtn;
    Spinner spinnerRol;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        nombreField = findViewById(R.id.editTextNombre);
        registerBtn = findViewById(R.id.buttonRegister);
        backToLoginBtn = findViewById(R.id.backButton);
        spinnerRol = findViewById(R.id.spinnerRol);
        mAuth = FirebaseAuth.getInstance();

        // 🎨 Adaptador personalizado para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // diseño personalizado
                getResources().getStringArray(R.array.roles_array)
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerRol.setAdapter(adapter);

        // 🔐 REGISTRARSE
        registerBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String nombre = nombreField.getText().toString().trim();
            String rolSeleccionado = spinnerRol.getSelectedItem().toString();

            // ❌ Bloquear registro como administrador
            if (rolSeleccionado.equals("admin")) {
                Toast.makeText(this, "No puedes registrarte como administrador", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Validación del rol
            if (rolSeleccionado.equals("Selecciona el rol")) {
                Toast.makeText(this, "Por favor selecciona un rol válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validaciones generales
            if (TextUtils.isEmpty(nombre)) {
                nombreField.setError("Este campo es obligatorio");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailField.setError("Este campo es obligatorio");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Correo inválido");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordField.setError("Este campo es obligatorio");
                return;
            }

            if (password.length() < 6) {
                passwordField.setError("Debe tener al menos 6 caracteres");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Guardar datos en Firebase Realtime Database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
                                String uid = user.getUid();

                                HashMap<String, Object> userData = new HashMap<>();
                                userData.put("correo", email);
                                userData.put("nombre", nombre);
                                userData.put("rol", rolSeleccionado);

                                ref.child(uid).setValue(userData)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(verifyTask -> {
                                                            if (verifyTask.isSuccessful()) {
                                                                Toast.makeText(this, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_LONG).show();
                                                                startActivity(new Intent(this, LoginActivity.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(this, "Error al enviar verificación", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            String mensaje = "Error: " + task.getException().getMessage();

                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                    mensaje = "Este correo ya está registrado. Intenta iniciar sesión.";
                                }
                            }

                            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 🔙 VOLVER AL LOGIN
        backToLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
