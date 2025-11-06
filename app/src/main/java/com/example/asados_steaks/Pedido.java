package com.example.asados_steaks;
import com.example.asados_steaks.Producto;
import java.util.ArrayList;



public class Pedido {
    public String clienteId;
    public String estado;
    public String fecha;
    public ArrayList<Producto> productos;
    public Ubicacion ubicacion;
    public Integer calificacion;

    public Pedido() {}
}
