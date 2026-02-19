package org.facturacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lineafactura")
public class LineaFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "articulo_codigo")
    private Articulo articulo;

    private int cantidad;
    private double subtotal;

    public LineaFactura() {}

    public LineaFactura(Factura factura, Articulo articulo, int cantidad, double subtotal) {
        this.factura = factura;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public Factura getFactura() { return factura; }
    public Articulo getArticulo() { return articulo; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }

    public void setFactura(Factura factura) { this.factura = factura; }
    public void setArticulo(Articulo articulo) { this.articulo = articulo; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
