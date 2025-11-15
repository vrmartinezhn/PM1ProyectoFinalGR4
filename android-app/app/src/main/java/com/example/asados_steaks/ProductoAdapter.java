package com.example.asados_steaks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        holder.textNombre.setText(producto.getNombre());
        holder.textPrecio.setText("L. " + producto.getPrecio());
        holder.textCantidad.setText(String.valueOf(producto.getCantidad()));
        holder.imagePlatillo.setImageResource(producto.getImagenId());

        holder.buttonMas.setOnClickListener((View v) -> {
            producto.setCantidad(producto.getCantidad() + 1);
            holder.textCantidad.setText(String.valueOf(producto.getCantidad()));
        });

        holder.buttonMenos.setOnClickListener((View v) -> {
            if (producto.getCantidad() > 0) {
                producto.setCantidad(producto.getCantidad() - 1);
                holder.textCantidad.setText(String.valueOf(producto.getCantidad()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecio, textCantidad;
        ImageButton buttonMas, buttonMenos;
        ImageView imagePlatillo;

        public ViewHolder(View itemView) {
            super(itemView);
            imagePlatillo = itemView.findViewById(R.id.imagePlatillo);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            buttonMas = itemView.findViewById(R.id.buttonMas);
            buttonMenos = itemView.findViewById(R.id.buttonMenos);
        }
    }
}