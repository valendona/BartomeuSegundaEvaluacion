package org.facturacion.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "cliente_nif")
    private Cliente cliente;

    private String fecha;   // ← AÑADIDO
    private double iva;
    private double total;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<LineaFactura> lineas = new ArrayList<>();

    public Factura() {}

    // ← CONSTRUCTOR COMPLETO
    public Factura(Cliente cliente, String fecha, double iva, double total) {
        this.id = "F" + System.currentTimeMillis();
        this.cliente = cliente;
        this.fecha = fecha;
        this.iva = iva;
        this.total = total;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public String getFecha() { return fecha; }   // ← GETTER AÑADIDO
    public double getIva() { return iva; }
    public double getTotal() { return total; }
    public List<LineaFactura> getLineas() { return lineas; }

    public void addLinea(LineaFactura linea) {
        lineas.add(linea);
        linea.setFactura(this);
    }
}
