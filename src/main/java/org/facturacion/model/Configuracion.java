package org.facturacion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    private String clave;

    private double valor;

    public Configuracion() {}

    public Configuracion(String clave, double valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public String getClave() { return clave; }
    public double getValor() { return valor; }

    public void setClave(String clave) { this.clave = clave; }
    public void setValor(double valor) { this.valor = valor; }
}
