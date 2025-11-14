package com.example.asados_steaks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> listaPedidos;

    public PedidoAdapter(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.textEstado.setText("Estado: " + pedido.estado);
        holder.textFecha.setText("Fecha: " + pedido.fecha);
        holder.textProductos.setText("Productos: " + pedido.descripcion);
        holder.textTotal.setText("Total: L. " + pedido.total);
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textEstado, textFecha, textProductos, textTotal;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textEstado = itemView.findViewById(R.id.textEstado);
            textFecha = itemView.findViewById(R.id.textFecha);
            textProductos = itemView.findViewById(R.id.textProductos);
            textTotal = itemView.findViewById(R.id.textTotal);
        }
    }
}