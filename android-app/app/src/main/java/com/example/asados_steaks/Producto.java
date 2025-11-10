package com.example.asados_steaks;

import java.io.Serializable;

public class Producto implements Serializable {
    public String nombre;
    public int cantidad;
    public double precio;
    public String descripcion;
    public int imagenResId; // ID del recurso local (no se guarda en Firebase)

    public Producto() {
        // Constructor vacío requerido por Firebase
    }

    public Producto(String nombre, int cantidad, double precio, String descripcion, int imagenResId) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenResId = imagenResId;
    }
}
