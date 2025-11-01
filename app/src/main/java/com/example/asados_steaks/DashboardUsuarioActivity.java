package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardUsuarioActivity extends AppCompatActivity {

    Button buttonPedidosActivos, buttonNuevoPedido, buttonPerfil, buttonRastreo, buttonHistorial, buttonCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_usuario);

        buttonPedidosActivos = findViewById(R.id.buttonPedidosActivos);
        buttonNuevoPedido = findViewById(R.id.buttonNuevoPedido);
        buttonPerfil = findViewById(R.id.buttonPerfil);
        buttonRastreo = findViewById(R.id.buttonRastreo);
        buttonHistorial = findViewById(R.id.buttonHistorial);
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion);

        buttonPedidosActivos.setOnClickListener(v -> {
          //  startActivity(new Intent(this, PedidosActivosActivity.class));
        });

        buttonNuevoPedido.setOnClickListener(v -> {
        //    startActivity(new Intent(this, NuevoPedidoActivity.class));
        });

        buttonPerfil.setOnClickListener(v -> {
          //  startActivity(new Intent(this, PerfilUsuarioActivity.class));
        });

        buttonRastreo.setOnClickListener(v -> {
          //  startActivity(new Intent(this, RastreoPedidoActivity.class));
        });

        buttonHistorial.setOnClickListener(v -> {
            //startActivity(new Intent(this, HistorialPedidosActivity.class));
        });

        buttonCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
