package com.example.asados_steaks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class DashboardRepartidorActivity extends AppCompatActivity {

    RecyclerView recyclerPedidos;
    Button buttonCerrarPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_repartidor);

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        buttonCerrarPedido = findViewById(R.id.buttonCerrarPedido);

        // Simular pedidos asignados
        ArrayList<String> pedidos = new ArrayList<>();
        pedidos.add("Pedido #1 - Skarleth - 9876-5432");
        pedidos.add("Pedido #2 - Carlos - 9123-4567");
        pedidos.add("Pedido #3 - Ana - 9988-1122");

        // Configurar RecyclerView
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setAdapter(new PedidoAdapter(pedidos));

        // Acción del botón
        buttonCerrarPedido.setOnClickListener(v -> {
            Toast.makeText(this, "Pedido marcado como entregado ✅", Toast.LENGTH_SHORT).show();
        });
    }
}
