package com.example.asados_steaks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class DashboardUsuarioActivity extends AppCompatActivity {

    RecyclerView recyclerPedidos;
    ArrayList<Pedido> listaPedidos;
    PedidoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_usuario);

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));

        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(listaPedidos);
        recyclerPedidos.setAdapter(adapter);

        // Botón flotante: Nuevo Pedido
        FloatingActionButton fabNuevoPedido = findViewById(R.id.fabNuevoPedido);
        fabNuevoPedido.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoPedidoActivity.class);
            startActivity(intent);
        });

        // Botón flotante: Perfil de Usuario
        FloatingActionButton fabPerfilUsuario = findViewById(R.id.fabPerfilUsuario);
        fabPerfilUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilUsuarioActivity.class);
            startActivity(intent);
        });

        cargarPedidos();
    }

    private void cargarPedidos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos");

        ref.orderByChild("clienteId").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listaPedidos.clear();
                        for (DataSnapshot pedidoSnap : snapshot.getChildren()) {
                            Pedido pedido = pedidoSnap.getValue(Pedido.class);
                            listaPedidos.add(pedido);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(DashboardUsuarioActivity.this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}