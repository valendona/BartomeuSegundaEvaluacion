package org.facturacion.ui;

import org.facturacion.dao.*;
import org.facturacion.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentanaAlbaranes extends JPanel {

    private final JComboBox<String> comboClientes, comboArticulos;
    private final JTextField txtCantidad;
    private final JTable tablaLineas, tablaAlbaranes;

    private final DefaultTableModel modeloLineas, modeloAlbaranes;
    private JComboBox<String> comboOrdenarAlbaranes;
    private JComboBox<String> comboBuscarAlbaranes;
    private JTextField txtBuscarAlbaranes;

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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPanel.setBackground(Color.WHITE);
        buscarPanel.add(new JLabel("Buscar por:"));
        comboBuscarAlbaranes = new JComboBox<>();
        txtBuscarAlbaranes = new JTextField(16);
        JButton btnBuscarAl = new JButton("Buscar"); btnBuscarAl.addActionListener(e -> buscarAlbaranes());
        JButton btnLimpiarAl = new JButton("Limpiar"); btnLimpiarAl.addActionListener(e -> { txtBuscarAlbaranes.setText(""); cargarAlbaranes(); });
        buscarPanel.add(comboBuscarAlbaranes); buscarPanel.add(txtBuscarAlbaranes); buscarPanel.add(btnBuscarAl); buscarPanel.add(btnLimpiarAl);

        JPanel ordenPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ordenPanel.setBackground(Color.WHITE);
        ordenPanel.add(new JLabel("Ordenar por:"));
        comboOrdenarAlbaranes = new JComboBox<>();
        comboOrdenarAlbaranes.addActionListener(e -> {int idx = comboOrdenarAlbaranes.getSelectedIndex(); if (idx>=0) ordenarAlbaranesPorColumna(idx);});
        ordenPanel.add(comboOrdenarAlbaranes);

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setBackground(Color.WHITE);
        topControls.add(buscarPanel, BorderLayout.WEST);
        topControls.add(ordenPanel, BorderLayout.EAST);

        header.add(topControls, BorderLayout.NORTH);
        header.add(split, BorderLayout.CENTER);
        contenedorCentral.add(header, BorderLayout.CENTER);

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
                    String.format(Locale.ENGLISH, "%.2f", a.getIva()),
                    String.format(Locale.ENGLISH, "%.2f", a.getTotal()),
                    a.isFacturado() ? "Sí" : "No"
            });
        }
        poblarComboOrdenarAlbaranes();
        poblarComboBuscarAlbaranes();
    }

    private void poblarComboOrdenarAlbaranes(){
        comboOrdenarAlbaranes.removeAllItems();
        for (int i=0;i<modeloAlbaranes.getColumnCount();i++) comboOrdenarAlbaranes.addItem(modeloAlbaranes.getColumnName(i));
        comboOrdenarAlbaranes.setSelectedIndex(-1);
    }

    private void poblarComboBuscarAlbaranes(){
        comboBuscarAlbaranes.removeAllItems();
        for (int i=0;i<modeloAlbaranes.getColumnCount();i++) comboBuscarAlbaranes.addItem(modeloAlbaranes.getColumnName(i));
        comboBuscarAlbaranes.setSelectedIndex(-1);
    }

    private void ordenarAlbaranesPorColumna(int colIndex){
        int rows = modeloAlbaranes.getRowCount();
        java.util.List<Object[]> datos = new java.util.ArrayList<>();
        for (int r=0;r<rows;r++){ Object[] fila = new Object[modeloAlbaranes.getColumnCount()]; for (int c=0;c<fila.length;c++) fila[c]=modeloAlbaranes.getValueAt(r,c); datos.add(fila);}
        java.util.Comparator<Object[]> cmp = (a,b)->{ Object va=a[colIndex], vb=b[colIndex]; if (va==null) va=""; if (vb==null) vb=""; try{ Double da=Double.valueOf(va.toString()); Double db=Double.valueOf(vb.toString()); return da.compareTo(db);}catch(Exception ex){return va.toString().toLowerCase().compareTo(vb.toString().toLowerCase());}};
        datos.sort(cmp);
        modeloAlbaranes.setRowCount(0);
        for (Object[] f:datos) modeloAlbaranes.addRow(f);
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
                String.format(Locale.ENGLISH, "%.2f", articulo.getPrecio()),
                String.format(Locale.ENGLISH, "%.2f", subtotal)
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
        // Cargar el albarán junto con sus líneas dentro de la sesión para evitar LazyInitialization
        Albaran albaran = albaranDAO.buscarPorIdConLineas(id);

        if (albaran == null) {
            JOptionPane.showMessageDialog(this, "Albarán no encontrado");
            return;
        }

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

        // Convertir líneas de albarán a líneas de factura
        List<LineaFactura> lineasAFacturar = new ArrayList<>();
        for (LineaAlbaran la : albaran.getLineas()) {
            LineaFactura lf = new LineaFactura(
                    factura,
                    la.getArticulo(),
                    la.getCantidad(),
                    la.getSubtotal()
            );
            lineasAFacturar.add(lf);
        }

        try {
            // Guardar factura y líneas en una sola transacción que también actualiza stock
            facturaDAO.guardarConLineas(factura, lineasAFacturar);

            // Marcar albarán como facturado solo si la operación anterior fue exitosa
            albaran.setFacturado(true);
            albaranDAO.actualizar(albaran);

            JOptionPane.showMessageDialog(this, "Albarán convertido en factura correctamente");

            cargarAlbaranes();
            // Recargar artículos para mostrar stock actualizado
            cargarArticulos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error convirtiendo el albarán: " + e.getMessage());
        }
    }

    private void buscarAlbaranes(){
        int col = comboBuscarAlbaranes.getSelectedIndex();
        String q = txtBuscarAlbaranes.getText();
        if (col<0 || q==null || q.isBlank()){ cargarAlbaranes(); return; }
        q = q.toLowerCase(); modeloAlbaranes.setRowCount(0);
        for (Albaran a: albaranDAO.listarTodos()){
            Object[] row = new Object[]{ a.getId(), a.getCliente().getNif(), a.getFecha(), String.format(Locale.ENGLISH, "%.2f", a.getIva()), String.format(Locale.ENGLISH, "%.2f", a.getTotal()), a.isFacturado()?"Sí":"No" };
            Object field = row[col]; String s = field==null?"":field.toString().toLowerCase(); if (s.contains(q)) modeloAlbaranes.addRow(row);
        }
    }
}
