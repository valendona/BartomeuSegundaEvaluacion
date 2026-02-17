package org.facturacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lineas_factura")
public class LineaFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Articulo articulo;

    private int cantidad;

    private double subtotal;

    public LineaFactura() {}

    public LineaFactura(Articulo articulo, int cantidad, double subtotal) {
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public Articulo getArticulo() { return articulo; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
}
