package org.facturacion.ui;

import org.facturacion.dao.*;
import org.facturacion.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaAlbaranes extends JPanel {

    private final JComboBox<String> comboClientes, comboArticulos;
    private final JTextField txtCantidad;
    private final JTable tablaLineas, tablaAlbaranes;

    private final DefaultTableModel modeloLineas, modeloAlbaranes;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private final AlbaranDAO albaranDAO = new AlbaranDAO();
    private final LineaAlbaranDAO lineaDAO = new LineaAlbaranDAO();
    private final ConfiguracionDAO configDAO = new ConfiguracionDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final LineaFacturaDAO lineaFacturaDAO = new LineaFacturaDAO();

    private final List<LineaAlbaran> lineasActuales = new ArrayList<>();

    public VentanaAlbaranes() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------------------------------------------------------
        // PANEL SUPERIOR (FORMULARIO)
        // ---------------------------------------------------------
        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 10, 10));
        panelSuperior.setBackground(Color.WHITE);

        panelSuperior.add(new JLabel("Cliente:"));
        comboClientes = new JComboBox<>();
        panelSuperior.add(comboClientes);

        panelSuperior.add(new JLabel("Artículo:"));
        comboArticulos = new JComboBox<>();
        panelSuperior.add(comboArticulos);

        panelSuperior.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        panelSuperior.add(txtCantidad);

        // ESPACIO EXTRA ENTRE FORMULARIO Y TABLAS
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(panelSuperior, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // BOTONES INFERIORES
        // ---------------------------------------------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregarLinea = new JButton("Agregar línea");
        btnAgregarLinea.addActionListener(e -> agregarLinea());
        panelBotones.add(btnAgregarLinea);

        JButton btnGuardar = new JButton("Guardar albarán");
        btnGuardar.addActionListener(e -> guardarAlbaran());
        panelBotones.add(btnGuardar);

        JButton btnEliminar = new JButton("Eliminar albarán");
        btnEliminar.addActionListener(e -> eliminarAlbaran());
        panelBotones.add(btnEliminar);

        JButton btnConvertir = new JButton("Convertir a factura");
        btnConvertir.addActionListener(e -> convertirAFactura());
        panelBotones.add(btnConvertir);

        add(panelBotones, BorderLayout.SOUTH);

        // ---------------------------------------------------------
        // TABLAS
        // ---------------------------------------------------------
        modeloLineas = new DefaultTableModel(
                new String[]{"Artículo", "Cantidad", "Precio", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaLineas = new JTable(modeloLineas);
        tablaLineas.setRowHeight(25);

        modeloAlbaranes = new DefaultTableModel(
                new String[]{"ID", "Cliente (NIF)", "Fecha", "IVA", "Total", "Facturado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaAlbaranes = new JTable(modeloAlbaranes);
        tablaAlbaranes.setRowHeight(25);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaLineas),
                new JScrollPane(tablaAlbaranes)
        );
        split.setDividerLocation(200);

        // ---------------------------------------------------------
        // CONTENEDOR CENTRAL CON ESPACIO SUPERIOR
        // ---------------------------------------------------------
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // ← ESPACIO REAL
        contenedorCentral.add(split, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // CARGA DE DATOS
        // ---------------------------------------------------------
        cargarClientes();
        cargarArticulos();
        cargarAlbaranes();
    }

    private void cargarClientes() {
        comboClientes.removeAllItems();
        for (Cliente c : clienteDAO.listarTodos()) {
            comboClientes.addItem(c.getNif() + " - " + c.getNombre() + " " + c.getApellido());
        }
    }

    private void cargarArticulos() {
        comboArticulos.removeAllItems();
        for (Articulo a : articuloDAO.listarTodos()) {
            comboArticulos.addItem(a.getCodigo() + " - " + a.getNombre());
        }
    }

    private void cargarAlbaranes() {
        modeloAlbaranes.setRowCount(0);
        for (Albaran a : albaranDAO.listarTodos()) {
            modeloAlbaranes.addRow(new Object[]{
                    a.getId(),
                    a.getCliente().getNif(),
                    a.getFecha(),
                    a.getIva(),
                    a.getTotal(),
                    a.isFacturado() ? "Sí" : "No"
            });
        }
    }

    private void agregarLinea() {
        if (txtCantidad.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Introduce una cantidad");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
            return;
        }

        String seleccionado = (String) comboArticulos.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo");
            return;
        }
        String codigoArticulo = seleccionado.split(" - ")[0];

        Articulo articulo = articuloDAO.buscarPorCodigo(codigoArticulo);

        double subtotal = articulo.getPrecio() * cantidad;

        modeloLineas.addRow(new Object[]{
                articulo.getNombre(),
                cantidad,
                articulo.getPrecio(),
                subtotal
        });

        lineasActuales.add(new LineaAlbaran(null, articulo, cantidad, subtotal));
    }

    private void guardarAlbaran() {

        if (lineasActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El albarán no tiene líneas");
            return;
        }

        String seleccionado = (String) comboClientes.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente");
            return;
        }
        String nif = seleccionado.split(" - ")[0];

        Cliente cliente = clienteDAO.buscarPorNif(nif);

        double iva = configDAO.getIVA();
        double totalSinIva = lineasActuales.stream().mapToDouble(LineaAlbaran::getSubtotal).sum();
        double total = totalSinIva + (totalSinIva * iva / 100);

        String fecha = LocalDate.now().toString();

        Albaran albaran = new Albaran(cliente, fecha, iva, total);
        albaranDAO.guardar(albaran);

        for (LineaAlbaran la : lineasActuales) {
            la.setAlbaran(albaran);
            lineaDAO.guardar(la);
        }

        JOptionPane.showMessageDialog(this, "Albarán guardado correctamente");

        modeloLineas.setRowCount(0);
        lineasActuales.clear();
        cargarAlbaranes();
    }

    private void eliminarAlbaran() {
        int fila = tablaAlbaranes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un albarán para eliminar");
            return;
        }

        String id = (String) modeloAlbaranes.getValueAt(fila, 0);
        Albaran albaran = albaranDAO.buscarPorId(id);

        if (albaran.isFacturado()) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar un albarán ya facturado");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres eliminar el albarán " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        albaranDAO.eliminar(albaran);

        JOptionPane.showMessageDialog(this, "Albarán eliminado correctamente");
        cargarAlbaranes();
    }

    private void convertirAFactura() {
        int fila = tablaAlbaranes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un albarán");
            return;
        }

        String id = (String) modeloAlbaranes.getValueAt(fila, 0);
        Albaran albaran = albaranDAO.buscarPorId(id);

        if (albaran.isFacturado()) {
            JOptionPane.showMessageDialog(this, "Este albarán ya está facturado");
            return;
        }

        String fecha = LocalDate.now().toString();

        Factura factura = new Factura(
                albaran.getCliente(),
                fecha,
                albaran.getIva(),
                albaran.getTotal()
        );

        facturaDAO.guardar(factura);

        for (LineaAlbaran la : albaran.getLineas()) {
            LineaFactura lf = new LineaFactura(
                    factura,
                    la.getArticulo(),
                    la.getCantidad(),
                    la.getSubtotal()
            );
            lineaFacturaDAO.guardar(lf);
        }

        albaran.setFacturado(true);
        albaranDAO.actualizar(albaran);

        JOptionPane.showMessageDialog(this, "Albarán convertido en factura correctamente");

        cargarAlbaranes();
    }
}
