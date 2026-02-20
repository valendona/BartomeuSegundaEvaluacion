package org.facturacion.io;

import java.util.List;

public class FacturaDTO {
    public String id;
    public String fecha;
    public double iva;
    public double total;
    public String clienteNif;
    public List<LineaFacturaDTO> lineas;

    public FacturaDTO() {}
}

