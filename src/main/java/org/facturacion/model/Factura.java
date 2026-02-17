package org.facturacion.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    private String id;

    @ManyToOne
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LineaFactura> lineas;

    private double iva; // IVA aplicado en esta factura

    public Factura() {}

    public Factura(Cliente cliente, List<LineaFactura> lineas, double iva) {
        this.id = generarIdFactura();
        this.cliente = cliente;
        this.lineas = lineas;
        this.iva = iva;
    }

    public static String generarIdFactura() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("F");

        for (int i = 0; i < 5; i++) {
            int index = (int) (Math.random() * caracteres.length());
            sb.append(caracteres.charAt(index));
        }

        return sb.toString();
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<LineaFactura> getLineas() { return lineas; }
    public double getIva() { return iva; }
}
