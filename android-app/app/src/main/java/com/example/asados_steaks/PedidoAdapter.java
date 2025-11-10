package com.example.asados_steaks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {

    ArrayList<Pedido> pedidos;

    public PedidoAdapter(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.textEstado.setText("Estado: " + pedido.estado);
        holder.textFecha.setText("Fecha: " + pedido.fecha);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEstado, textFecha;

        public ViewHolder(View itemView) {
            super(itemView);
            textEstado = itemView.findViewById(R.id.textEstado);
            textFecha = itemView.findViewById(R.id.textFecha);
        }
    }
}
