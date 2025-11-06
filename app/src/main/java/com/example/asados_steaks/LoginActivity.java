package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton, resetPasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        auth.signOut(); // Limpia sesión previa

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        Button btnClaveTemporal = findViewById(R.id.buttonClaveTemporal);

        btnClaveTemporal.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ReenviarClaveActivity.class));
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        resetPasswordButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "El correo electrónico no tiene un formato válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
                                ref.orderByChild("correo").equalTo(user.getEmail().toLowerCase())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dato : snapshot.getChildren()) {
                                                        String rol = dato.child("rol").getValue(String.class);
                                                        if (rol != null) {
                                                            switch (rol.toLowerCase()) {
                                                                case "usuario":
                                                                    startActivity(new Intent(LoginActivity.this, DashboardUsuarioActivity.class));
                                                                    finish();
                                                                    break;
                                                                case "repartidor":
                                                                    startActivity(new Intent(LoginActivity.this, DashboardRepartidorActivity.class));
                                                                    finish();
                                                                    break;
                                                                case "admin":
                                                                    Toast.makeText(LoginActivity.this, "Acceso solo desde la plataforma web", Toast.LENGTH_LONG).show();
                                                                    auth.signOut();
                                                                    break;
                                                                default:
                                                                    Toast.makeText(LoginActivity.this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                                                                    auth.signOut();
                                                                    break;
                                                            }
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                                            auth.signOut();
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Usuario no registrado en la base de datos", Toast.LENGTH_SHORT).show();
                                                    auth.signOut();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, "Error al cargar rol", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Verifica tu correo antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                                auth.signOut();
                            }
                        } else {
                            Exception exception = task.getException();
                            String mensaje;

                            if (exception != null) {
                                String errorMsg = exception.getMessage();
                                if (errorMsg != null) {
                                    errorMsg = errorMsg.toLowerCase();

                                    if (errorMsg.contains("password")) {
                                        mensaje = "Contraseña incorrecta. ¿La cambiaste recientemente?";
                                    } else if (errorMsg.contains("email")) {
                                        mensaje = "Este correo no está registrado. Regístrate primero.";
                                    } else if (errorMsg.contains("network")) {
                                        mensaje = "Error de conexión. Verifica tu internet.";
                                    } else if (errorMsg.contains("disabled")) {
                                        mensaje = "Esta cuenta ha sido desactivada.";
                                    } else if (errorMsg.contains("too many requests")) {
                                        mensaje = "Demasiados intentos. Intenta más tarde.";
                                    } else {
                                        mensaje = "Error inesperado: " + exception.getMessage();
                                    }
                                } else {
                                    mensaje = "Error inesperado. Intenta nuevamente.";
                                }
                            } else {
                                mensaje = "Error inesperado. Intenta nuevamente.";
                            }

                            Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                    });
        });
    }

    // 🔄 PERSISTENCIA DE SESIÓN CORREGIDA
    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");
                ref.orderByChild("correo").equalTo(currentUser.getEmail().toLowerCase())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dato : snapshot.getChildren()) {
                                        String rol = dato.child("rol").getValue(String.class);
                                        if (rol != null) {
                                            switch (rol.toLowerCase()) {
                                                case "usuario":
                                                    startActivity(new Intent(LoginActivity.this, DashboardUsuarioActivity.class));
                                                    finish();
                                                    break;
                                                case "repartidor":
                                                    startActivity(new Intent(LoginActivity.this, DashboardRepartidorActivity.class));
                                                    finish();
                                                    break;
                                                case "admin":
                                                    Toast.makeText(LoginActivity.this, "Acceso solo desde la plataforma web", Toast.LENGTH_LONG).show();
                                                    auth.signOut();
                                                    break;
                                                default:
                                                    Toast.makeText(LoginActivity.this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                                                    auth.signOut();
                                                    break;
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        }
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Usuario no registrado en la base de datos", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(LoginActivity.this, "Error al cargar rol", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Tu correo no está verificado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                auth.signOut();
            }
        }
    }
}
