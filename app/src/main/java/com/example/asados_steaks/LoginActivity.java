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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton, resetPasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        auth.signOut(); // 👈 Esto borra cualquier sesión activa

        // Estilo de barra de estado clara
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        // Inicializar vistas
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // 🔐 INICIAR SESIÓN
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(LoginActivity.this, "El correo electrónico no tiene un formato válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // ✅ Validar el rol desde Firebase
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid());

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        String rol = snapshot.child("rol").getValue(String.class);

                                        if (rol != null) {
                                            switch (rol) {
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
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Error al cargar rol", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Verifica tu correo antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                                auth.signOut(); // 👈 Cierra sesión si no está verificado
                            }
                        } else {
                            Exception exception = task.getException();
                            String mensaje;

                            if (exception instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) exception).getErrorCode();

                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        mensaje = "El formato del correo es inválido.";
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        mensaje = "Este correo no está registrado. Regístrate primero.";
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        mensaje = "Contraseña incorrecta. ¿La cambiaste recientemente?";
                                        break;
                                    case "ERROR_USER_DISABLED":
                                        mensaje = "Esta cuenta ha sido desactivada.";
                                        break;
                                    case "ERROR_TOO_MANY_REQUESTS":
                                        mensaje = "Demasiados intentos. Intenta más tarde.";
                                        break;
                                    case "ERROR_NETWORK_REQUEST_FAILED":
                                        mensaje = "Error de conexión. Verifica tu internet.";
                                        break;
                                    default:
                                        mensaje = "Error inesperado: " + exception.getMessage();
                                        break;
                                }
                            } else {
                                mensaje = "Error inesperado: " + exception.getMessage();
                            }

                            Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 📝 IR A LA PANTALLA DE REGISTRO
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 🔁 RESETEAR CONTRASEÑA
        resetPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    // 🔄 PERSISTENCIA DE SESIÓN CORREGIDA
    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance(); // Aseguramos que esté inicializado
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(currentUser.getUid());

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String rol = snapshot.child("rol").getValue(String.class);

                        if (rol != null) {
                            switch (rol) {
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Error al cargar rol", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Tu correo no está verificado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                auth.signOut(); // Cierra sesión si no está verificado
            }
        }
    }
}
