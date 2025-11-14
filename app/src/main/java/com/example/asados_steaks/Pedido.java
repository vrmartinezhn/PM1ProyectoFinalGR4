package com.example.asados_steaks;

public class Pedido {
    public String clienteId;       // ID del usuario que hizo el pedido
    public String estado;          // Estado del pedido (nuevo, en cocina, entregado, etc.)
    public String fecha;           // Fecha del pedido (formato yyyy-MM-dd)
    public String descripcion;     // Texto con productos y cantidades (ej: "Parrillada x2, Pollo x1")
    public int total;              // Total a pagar por el pedido
    public Ubicacion ubicacion;    // Ubicación GPS del cliente
    public Integer calificacion;   // Calificación del pedido (1 a 5 estrellas, puede ser null)

    public Pedido() {
        // Constructor vacío requerido por Firebase
    }
}
