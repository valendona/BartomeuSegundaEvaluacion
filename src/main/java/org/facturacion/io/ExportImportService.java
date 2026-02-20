package org.facturacion.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.facturacion.dao.ArticuloDAO;
import org.facturacion.dao.ClienteDAO;
import org.facturacion.dao.FacturaDAO;
import org.facturacion.model.Articulo;
import org.facturacion.model.Cliente;
import org.facturacion.model.Factura;
import org.facturacion.model.LineaFactura;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportImportService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();

    public void exportClientesToJson(File outFile) throws ImportExportException {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            ObjectMapper mapper = JsonMapperFactory.create();
            mapper.writeValue(outFile, clientes);
        } catch (IOException e) {
            throw new ImportExportException("Error escribiendo clientes a JSON", e);
        }
    }

    public void exportArticulosToJson(File outFile) throws ImportExportException {
        try {
            List<Articulo> articulos = articuloDAO.listarTodos();
            ObjectMapper mapper = JsonMapperFactory.create();
            mapper.writeValue(outFile, articulos);
        } catch (IOException e) {
            throw new ImportExportException("Error escribiendo articulos a JSON", e);
        }
    }

    public void exportFacturasToJson(File outFile) throws ImportExportException {
        try {
            List<Factura> facturas = facturaDAO.listarTodas();
            List<FacturaDTO> dtos = new ArrayList<>();
            for (Factura f : facturas) {
                FacturaDTO dto = new FacturaDTO();
                dto.id = f.getId();
                dto.fecha = f.getFecha();
                dto.iva = f.getIva();
                dto.total = f.getTotal();
                dto.clienteNif = f.getCliente() != null ? f.getCliente().getNif() : null;
                dto.lineas = new ArrayList<>();
                for (LineaFactura lf : f.getLineas()) {
                    LineaFacturaDTO l = new LineaFacturaDTO();
                    l.articuloCodigo = lf.getArticulo() != null ? lf.getArticulo().getCodigo() : null;
                    l.cantidad = lf.getCantidad();
                    l.subtotal = lf.getSubtotal();
                    l.precioUnit = lf.getArticulo() != null ? lf.getArticulo().getPrecio() : 0.0;
                    dto.lineas.add(l);
                }
                dtos.add(dto);
            }

            ObjectMapper mapper = JsonMapperFactory.create();
            mapper.writeValue(outFile, dtos);
        } catch (IOException e) {
            throw new ImportExportException("Error escribiendo facturas a JSON", e);
        }
    }

    public int importClientesFromJson(File inFile, boolean upsert) throws ImportExportException {
        try {
            ObjectMapper mapper = JsonMapperFactory.create();
            List<Cliente> clientes = mapper.readValue(inFile, new TypeReference<List<Cliente>>() {});
            int count = 0;
            for (Cliente c : clientes) {
                if (c.getNif() == null || c.getNif().isBlank()) {
                    // Ignorar clientes sin NIF
                    continue;
                }
                if (clienteDAO.buscarPorNif(c.getNif()) == null) {
                    clienteDAO.guardar(c);
                    count++;
                } else if (upsert) {
                    clienteDAO.actualizar(c);
                    count++;
                }
            }
            return count;
        } catch (IOException e) {
            throw new ImportExportException("Error leyendo clientes desde JSON", e);
        }
    }

    public int importArticulosFromJson(File inFile, boolean upsert) throws ImportExportException {
        try {
            ObjectMapper mapper = JsonMapperFactory.create();
            List<Articulo> articulos = mapper.readValue(inFile, new TypeReference<List<Articulo>>() {});
            int count = 0;
            for (Articulo a : articulos) {
                if (a.getCodigo() == null || a.getCodigo().isBlank()) {
                    a.setCodigo("ART-" + System.currentTimeMillis());
                }
                if (articuloDAO.buscarPorCodigo(a.getCodigo()) == null) {
                    articuloDAO.guardar(a);
                    count++;
                } else if (upsert) {
                    articuloDAO.actualizar(a);
                    count++;
                }
            }
            return count;
        } catch (IOException e) {
            throw new ImportExportException("Error leyendo articulos desde JSON", e);
        }
    }

    public int importFacturasFromJson(File inFile, boolean upsert) throws ImportExportException {
        try {
            ObjectMapper mapper = JsonMapperFactory.create();
            List<FacturaDTO> dtos = mapper.readValue(inFile, new TypeReference<List<FacturaDTO>>() {});
            int count = 0;
            for (FacturaDTO dto : dtos) {
                // validar cliente
                if (dto.clienteNif == null || dto.clienteNif.isBlank()) continue;
                Cliente cliente = clienteDAO.buscarPorNif(dto.clienteNif);
                if (cliente == null) continue; // no podemos crear factura sin cliente existente

                Factura existing = facturaDAO.buscarPorId(dto.id);
                if (existing != null && !upsert) {
                    continue;
                }

                Factura f = new Factura();
                f.setId(dto.id != null ? dto.id : "F" + System.currentTimeMillis());
                f.setCliente(cliente);
                f.setFecha(dto.fecha);
                f.setIva(dto.iva);
                f.setTotal(dto.total);

                // crear lineas
                List<LineaFactura> lineas = new ArrayList<>();
                if (dto.lineas != null) {
                    for (LineaFacturaDTO l : dto.lineas) {
                        Articulo art = null;
                        if (l.articuloCodigo != null) art = articuloDAO.buscarPorCodigo(l.articuloCodigo);
                        if (art == null) continue; // ignorar linea si artículo no existe
                        LineaFactura lf = new LineaFactura(f, art, l.cantidad, l.subtotal);
                        lineas.add(lf);
                    }
                }

                facturaDAO.guardarConLineas(f, lineas);
                count++;
            }
            return count;
        } catch (IOException e) {
            throw new ImportExportException("Error leyendo facturas desde JSON", e);
        }
    }
}
