package com.example.asados_steaks;

public class Producto {
    private String nombre;
    private int cantidad;
    private int precio;
    private String descripcion;
    private int imagenId;

    public Producto(String nombre, int cantidad, int precio, String descripcion, int imagenId) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenId = imagenId;
    }

    // ✅ Getters
    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagenId() {
        return imagenId;
    }

    // ✅ Setter para cantidad (si el usuario la modifica)
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}