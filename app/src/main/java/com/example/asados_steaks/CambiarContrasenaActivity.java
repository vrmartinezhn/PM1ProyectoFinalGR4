package com.example.asados_steaks;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContrasenaActivity extends AppCompatActivity {

    EditText nuevaClaveField;
    Button cambiarBtn, regresarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        nuevaClaveField = findViewById(R.id.editTextNuevaClave);
        cambiarBtn = findViewById(R.id.buttonCambiarClave);
        regresarBtn = findViewById(R.id.buttonRegresar);

        cambiarBtn.setOnClickListener(v -> {
            Log.d("CambiarClave", "Botón cambiar presionado");

            String nuevaClave = nuevaClaveField.getText().toString().trim();

            if (TextUtils.isEmpty(nuevaClave) || nuevaClave.length() < 6) {
                nuevaClaveField.setError("La contraseña debe tener al menos 6 caracteres");
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(nuevaClave)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            }
        });

        regresarBtn.setOnClickListener(v -> {
            Log.d("CambiarClave", "Botón regresar presionado");
            finish();
        });
    }
}
