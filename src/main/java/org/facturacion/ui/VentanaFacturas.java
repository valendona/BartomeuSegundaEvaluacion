package org.facturacion.ui;

import org.facturacion.dao.ArticuloDAO;
import org.facturacion.dao.ClienteDAO;
import org.facturacion.dao.FacturaDAO;
import org.facturacion.model.Articulo;
import org.facturacion.model.Cliente;
import org.facturacion.model.Factura;
import org.facturacion.model.LineaFactura;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class VentanaFacturas extends JPanel {

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Articulo> comboArticulos;
    private JTextField txtCantidad;

    private JTable tablaLineas;
    private DefaultTableModel modeloLineas;

    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;

    private JLabel lblTotal;

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ArticuloDAO articuloDAO = new ArticuloDAO();
    private FacturaDAO facturaDAO = new FacturaDAO();

    private List<LineaFactura> lineas = new ArrayList<>();

    public VentanaFacturas() {

        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(520);
        add(split, BorderLayout.CENTER);

        // PANEL IZQUIERDO
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Estilos.COLOR_FONDO);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(4, 2, 10, 10));
        panelSuperior.setBackground(Estilos.COLOR_FONDO);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(Estilos.FUENTE_NORMAL);
        panelSuperior.add(lblCliente);

        comboClientes = new JComboBox<>();
        comboClientes.setFont(Estilos.FUENTE_NORMAL);
        cargarClientes();
        panelSuperior.add(comboClientes);

        JLabel lblArticulo = new JLabel("Artículo:");
        lblArticulo.setFont(Estilos.FUENTE_NORMAL);
        panelSuperior.add(lblArticulo);

        comboArticulos = new JComboBox<>();
        comboArticulos.setFont(Estilos.FUENTE_NORMAL);
        cargarArticulos();
        panelSuperior.add(comboArticulos);

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(Estilos.FUENTE_NORMAL);
        panelSuperior.add(lblCantidad);

        txtCantidad = new JTextField();
        txtCantidad.setFont(Estilos.FUENTE_NORMAL);
        panelSuperior.add(txtCantidad);

        BotonEstilizado btnAñadir = new BotonEstilizado("Añadir a factura");
        btnAñadir.addActionListener(e -> añadirLinea());
        panelSuperior.add(btnAñadir);

        panelIzquierdo.add(panelSuperior, BorderLayout.NORTH);

        // TABLA DE LÍNEAS
        modeloLineas = new DefaultTableModel(new String[]{"Artículo", "Precio", "Cantidad", "Subtotal"}, 0);
        tablaLineas = crearTablaBonita(modeloLineas);

        panelIzquierdo.add(new JScrollPane(tablaLineas), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(Estilos.COLOR_FONDO);

        lblTotal = new JLabel("Total: 0.00 €");
        lblTotal.setFont(Estilos.FUENTE_TITULO);
        panelInferior.add(lblTotal);

        BotonEstilizado btnGuardar = new BotonEstilizado("Guardar Factura");
        btnGuardar.addActionListener(e -> guardarFactura());
        panelInferior.add(btnGuardar);

        BotonEstilizado btnEliminar = new BotonEstilizado("Eliminar Factura");
        btnEliminar.addActionListener(e -> eliminarFactura());
        panelInferior.add(btnEliminar);

        panelIzquierdo.add(panelInferior, BorderLayout.SOUTH);

        split.setLeftComponent(panelIzquierdo);

        // PANEL DERECHO
        modeloFacturas = new DefaultTableModel(
                new String[]{"ID", "Fecha", "Cliente", "IVA (%)", "Total"}, 0
        );
        tablaFacturas = crearTablaBonita(modeloFacturas);

        tablaFacturas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarFacturaSeleccionada();
            }
        });

        split.setRightComponent(new JScrollPane(tablaFacturas));

        cargarFacturas();
    }

    private JTable crearTablaBonita(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(Estilos.FUENTE_NORMAL);
        tabla.setRowHeight(25);

        // Cabecera
        tabla.getTableHeader().setFont(Estilos.FUENTE_BOTON);
        tabla.getTableHeader().setBackground(new Color(66, 133, 244));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(200, 220, 255));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                return c;
            }
        });

        return tabla;
    }

    private void cargarClientes() {
        for (Cliente c : clienteDAO.listarTodos()) comboClientes.addItem(c);
    }

    private void cargarArticulos() {
        for (Articulo a : articuloDAO.listarTodos()) comboArticulos.addItem(a);
    }

    private void añadirLinea() {
        try {
            Articulo articulo = (Articulo) comboArticulos.getSelectedItem();
            int cantidad = Integer.parseInt(txtCantidad.getText());

            double subtotal = articulo.getPrecio() * cantidad;

            LineaFactura linea = new LineaFactura(articulo, cantidad, subtotal);
            lineas.add(linea);

            modeloLineas.addRow(new Object[]{
                    articulo.getNombre(),
                    articulo.getPrecio(),
                    cantidad,
                    subtotal
            });

            actualizarTotal();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero");
        }
    }

    private void actualizarTotal() {
        double subtotal = lineas.stream().mapToDouble(LineaFactura::getSubtotal).sum();
        double iva = VentanaConfiguracion.getIVA();
        double total = subtotal + (subtotal * iva / 100);

        lblTotal.setText("Total: " + String.format("%.2f", total) + " €");
    }

    private void guardarFactura() {
        if (lineas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La factura está vacía");
            return;
        }

        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        double iva = VentanaConfiguracion.getIVA();

        Factura factura = new Factura(cliente, lineas, iva);

        facturaDAO.guardar(factura);

        JOptionPane.showMessageDialog(this, "Factura guardada correctamente");

        lineas = new ArrayList<>();
        modeloLineas.setRowCount(0);
        actualizarTotal();


        cargarFacturas();
    }

    private void eliminarFactura() {
        int fila = tablaFacturas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una factura para eliminar");
            return;
        }

        String idFactura = (String) modeloFacturas.getValueAt(fila, 0);

        Factura factura = facturaDAO.buscarPorId(idFactura);

        if (factura != null) {
            facturaDAO.eliminar(factura);
            JOptionPane.showMessageDialog(this, "Factura eliminada correctamente");
            cargarFacturas();
        }
    }

    private void cargarFacturas() {
        modeloFacturas.setRowCount(0);

        for (Factura f : facturaDAO.listarTodas()) {

            double subtotal = f.getLineas().stream()
                    .mapToDouble(LineaFactura::getSubtotal)
                    .sum();

            double total = subtotal + (subtotal * f.getIva() / 100);

            modeloFacturas.addRow(new Object[]{
                    f.getId(),
                    "Sin fecha",
                    (f.getCliente() != null ? f.getCliente().getDni() : "SIN CLIENTE"),
                    f.getIva(),
                    String.format("%.2f", total)
            });
        }
    }

    private void cargarFacturaSeleccionada() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) return;

        String idFactura = (String) modeloFacturas.getValueAt(fila, 0);
        Factura factura = facturaDAO.buscarPorId(idFactura);

        if (factura == null) return;

        lineas.clear();
        modeloLineas.setRowCount(0);

        for (LineaFactura l : factura.getLineas()) {
            lineas.add(l);

            modeloLineas.addRow(new Object[]{
                    l.getArticulo().getNombre(),
                    l.getArticulo().getPrecio(),
                    l.getCantidad(),
                    l.getSubtotal()
            });
        }

        double subtotal = factura.getLineas().stream()
                .mapToDouble(LineaFactura::getSubtotal)
                .sum();

        double total = subtotal + (subtotal * factura.getIva() / 100);

        lblTotal.setText("Total: " + String.format("%.2f", total) + " €");
    }
}
