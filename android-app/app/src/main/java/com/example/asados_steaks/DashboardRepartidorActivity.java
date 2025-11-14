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

        // Simular pedidos asignados como objetos Pedido
        ArrayList<Pedido> pedidos = new ArrayList<>();

        Pedido pedido1 = new Pedido();
        pedido1.estado = "En camino";
        pedido1.fecha = "2025-11-05";

        Pedido pedido2 = new Pedido();
        pedido2.estado = "En cocina";
        pedido2.fecha = "2025-11-04";

        Pedido pedido3 = new Pedido();
        pedido3.estado = "Nuevo";
        pedido3.fecha = "2025-11-03";

        pedidos.add(pedido1);
        pedidos.add(pedido2);
        pedidos.add(pedido3);

        // Configurar RecyclerView
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setAdapter(new PedidoAdapter(pedidos));

        // Acción del botón
        buttonCerrarPedido.setOnClickListener(v -> {
            Toast.makeText(this, "Pedido marcado como entregado ✅", Toast.LENGTH_SHORT).show();
        });
    }
}
