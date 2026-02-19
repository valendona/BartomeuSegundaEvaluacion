package org.facturacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lineaalbaran")
public class LineaAlbaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "albaran_id")
    private Albaran albaran;

    @ManyToOne
    @JoinColumn(name = "articulo_codigo")
    private Articulo articulo;

    private int cantidad;
    private double subtotal;

    public LineaAlbaran() {}

    public LineaAlbaran(Albaran albaran, Articulo articulo, int cantidad, double subtotal) {
        this.albaran = albaran;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public Albaran getAlbaran() { return albaran; }
    public Articulo getArticulo() { return articulo; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }

    public void setAlbaran(Albaran albaran) { this.albaran = albaran; }
}
