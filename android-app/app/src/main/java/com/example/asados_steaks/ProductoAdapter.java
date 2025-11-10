package com.example.asados_steaks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    ArrayList<Producto> productos;

    public ProductoAdapter(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.textNombre.setText(producto.nombre);
        holder.textPrecio.setText("L. " + producto.precio);
        holder.textCantidad.setText(String.valueOf(producto.cantidad));

        holder.buttonMas.setOnClickListener(v -> {
            producto.cantidad++;
            holder.textCantidad.setText(String.valueOf(producto.cantidad));
        });

        holder.buttonMenos.setOnClickListener(v -> {
            if (producto.cantidad > 0) {
                producto.cantidad--;
                holder.textCantidad.setText(String.valueOf(producto.cantidad));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecio, textCantidad;
        Button buttonMas, buttonMenos;

        public ViewHolder(View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            buttonMas = itemView.findViewById(R.id.buttonMas);
            buttonMenos = itemView.findViewById(R.id.buttonMenos);
        }
    }
}
