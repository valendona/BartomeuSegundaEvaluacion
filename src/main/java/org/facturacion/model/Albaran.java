package org.facturacion.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albaranes")
public class Albaran {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "cliente_nif")
    private Cliente cliente;

    private String fecha;
    private double iva;
    private double total;
    private boolean facturado;

    @OneToMany(mappedBy = "albaran", cascade = CascadeType.ALL)
    private List<LineaAlbaran> lineas = new ArrayList<>();
    //Default Constructor
    public Albaran() {}

    //Constructor
    public Albaran(Cliente cliente, String fecha, double iva, double total) {
        this.id = "A" + System.currentTimeMillis();
        this.cliente = cliente;
        this.fecha = fecha;
        this.iva = iva;
        this.total = total;
        this.facturado = false;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public String getFecha() { return fecha; }
    public double getIva() { return iva; }
    public double getTotal() { return total; }
    public boolean isFacturado() { return facturado; }
    public List<LineaAlbaran> getLineas() { return lineas; }

    public void setFacturado(boolean facturado) { this.facturado = facturado; }
}
