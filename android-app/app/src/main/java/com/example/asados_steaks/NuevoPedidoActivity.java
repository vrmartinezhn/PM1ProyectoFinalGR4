package com.example.asados_steaks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NuevoPedidoActivity extends AppCompatActivity {

    RecyclerView recyclerMenu;
    Button buttonConfirmar;
    ArrayList<Producto> productosSeleccionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        recyclerMenu = findViewById(R.id.recyclerMenu);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);

        productosSeleccionados = new ArrayList<>();

        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerMenu.setAdapter(new ProductoAdapter(productosSeleccionados));

        cargarMenu();

        buttonConfirmar.setOnClickListener(v -> guardarPedido());
    }

    private void cargarMenu() {
        productosSeleccionados.clear();

        productosSeleccionados.add(new Producto(
                "Parrillada familiar", 0, 450,
                "Carne de res, chuleta, pollo, chorizo, tajadas, frijoles",
                R.drawable.parrillada_familiar));

        productosSeleccionados.add(new Producto(
                "Plato de Res", 0, 135,
                "Carne de res a la parrilla, frijoles, queso, encurtido",
                R.drawable.plato_res));

        productosSeleccionados.add(new Producto(
                "Pollo a la plancha", 0, 120,
                "Pechuga de pollo, vegetales salteados, arroz",
                R.drawable.pollo_plancha));

        productosSeleccionados.add(new Producto(
                "Chuleta", 0, 110,
                "Chuleta de cerdo, tajadas, ensalada",
                R.drawable.chuleta));

        recyclerMenu.getAdapter().notifyDataSetChanged();
    }

    private void guardarPedido() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 🗓️ Fecha actual
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 🧮 Calcular total y descripción
        int total = 0;
        StringBuilder descripcionProductos = new StringBuilder();

        for (Producto p : productosSeleccionados) {
            if (p.getCantidad() > 0) {
                total += p.getCantidad() * p.getPrecio();
                descripcionProductos.append(p.getNombre())
                        .append(" x").append(p.getCantidad())
                        .append(", ");
            }
        }

        // 🛒 Crear pedido
        Pedido pedido = new Pedido();
        pedido.clienteId = uid;
        pedido.estado = "nuevo";
        pedido.fecha = fechaActual;
        pedido.descripcion = descripcionProductos.toString();
        pedido.total = total;
        pedido.ubicacion = new Ubicacion(15.832, -87.938); // temporal, luego GPS real
        pedido.calificacion = null;

        // 🚀 Enviar a Firebase
        FirebaseDatabase.getInstance().getReference("pedidos")
                .push().setValue(pedido)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Pedido enviado exitosamente 💖", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al enviar pedido", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}