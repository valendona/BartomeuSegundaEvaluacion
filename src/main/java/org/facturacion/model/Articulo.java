package org.facturacion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "articulos")
public class Articulo {

    @Id
    private String codigo;

    private String nombre;
    private double precio;
    private int stock;

    public Articulo() {}

    public Articulo(String nombre, double precio, int stock) {
        this.codigo = generarCodigo();
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    private String generarCodigo() {
        return "ART-" + System.currentTimeMillis();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
