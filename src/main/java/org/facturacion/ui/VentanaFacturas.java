package org.facturacion.ui;

import org.facturacion.dao.*;
import org.facturacion.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaFacturas extends JPanel {

    private final JComboBox<String> comboClientes, comboArticulos;
    private final JTextField txtCantidad;
    private final JTable tablaLineas, tablaFacturas;

    private final DefaultTableModel modeloLineas, modeloFacturas;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final LineaFacturaDAO lineaDAO = new LineaFacturaDAO();
    private final ConfiguracionDAO configDAO = new ConfiguracionDAO();

    private final List<LineaFactura> lineasActuales = new ArrayList<>();

    public VentanaFacturas() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------------------------------------------------------
        // FORMULARIO SUPERIOR
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

        // ESPACIO ENTRE FORMULARIO Y TABLAS
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

        JButton btnGuardar = new JButton("Guardar factura");
        btnGuardar.addActionListener(e -> guardarFactura());
        panelBotones.add(btnGuardar);

        JButton btnEliminar = new JButton("Eliminar factura");
        btnEliminar.addActionListener(e -> eliminarFactura());
        panelBotones.add(btnEliminar);

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

        modeloFacturas = new DefaultTableModel(
                new String[]{"ID", "Cliente (NIF)", "Fecha", "IVA", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setRowHeight(25);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaLineas),
                new JScrollPane(tablaFacturas)
        );
        split.setDividerLocation(200);

        // ---------------------------------------------------------
        // CONTENEDOR CENTRAL CON ESPACIO SUPERIOR
        // ---------------------------------------------------------
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contenedorCentral.add(split, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // CARGA DE DATOS
        // ---------------------------------------------------------
        cargarClientes();
        cargarArticulos();
        cargarFacturas();
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

    private void cargarFacturas() {
        modeloFacturas.setRowCount(0);
        for (Factura f : facturaDAO.listarTodas()) {   // ← MÉTODO CORRECTO
            modeloFacturas.addRow(new Object[]{
                    f.getId(),
                    f.getCliente().getNif(),
                    f.getFecha(),     // ← YA EXISTE
                    f.getIva(),
                    f.getTotal()
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

        lineasActuales.add(new LineaFactura(null, articulo, cantidad, subtotal));
    }

    private void guardarFactura() {

        if (lineasActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La factura no tiene líneas");
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
        double totalSinIva = lineasActuales.stream().mapToDouble(LineaFactura::getSubtotal).sum();
        double total = totalSinIva + (totalSinIva * iva / 100);

        String fecha = LocalDate.now().toString();

        // ← CONSTRUCTOR CORRECTO
        Factura factura = new Factura(
                cliente,
                fecha,
                iva,
                total
        );

        try {
            facturaDAO.guardarConLineas(factura, lineasActuales);

            JOptionPane.showMessageDialog(this, "Factura guardada correctamente");

            modeloLineas.setRowCount(0);
            lineasActuales.clear();
            cargarFacturas();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error guardando factura: " + e.getMessage());
        }
    }

    private void eliminarFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una factura para eliminar");
            return;
        }

        String id = (String) modeloFacturas.getValueAt(fila, 0);
        Factura factura = facturaDAO.buscarPorId(id);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres eliminar la factura " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        facturaDAO.eliminar(factura);

        JOptionPane.showMessageDialog(this, "Factura eliminada correctamente");
        cargarFacturas();
    }
}
