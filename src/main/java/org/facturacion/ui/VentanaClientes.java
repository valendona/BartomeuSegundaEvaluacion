package org.facturacion.ui;

import org.facturacion.dao.ClienteDAO;
import org.facturacion.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VentanaClientes extends JPanel {

    private final JTextField txtNif, txtNombre, txtApellido, txtDireccion, txtTelefono, txtEmail;
    private final JTable tablaClientes;
    private final DefaultTableModel modelo;
    private final JComboBox<String> comboOrdenar = new JComboBox<>();
    private JComboBox<String> comboBuscar;
    private JTextField txtBuscar;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public VentanaClientes() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Formulario Superior
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBackground(Color.WHITE);

        panelForm.add(new JLabel("NIF:"));
        txtNif = new JTextField();
        panelForm.add(txtNif);

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelForm.add(txtApellido);

        panelForm.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelForm.add(txtDireccion);

        panelForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelForm.add(txtTelefono);

        panelForm.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        // Espacio entre formulario y tabla
        panelForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(panelForm, BorderLayout.NORTH);

        // Botones Inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarCliente());
        panelBotones.add(btnGuardar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarCliente());
        panelBotones.add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelBotones.add(btnEliminar);

        // Botones de import/export JSON
        JButton btnExportJson = new JButton("Exportar JSON");
        btnExportJson.addActionListener(e -> exportarClientesJson());
        panelBotones.add(btnExportJson);

        JButton btnImportJson = new JButton("Importar JSON");
        btnImportJson.addActionListener(e -> importarClientesJson());
        panelBotones.add(btnImportJson);

        add(panelBotones, BorderLayout.SOUTH);

        // Tabla de clientes
        modelo = new DefaultTableModel(
                new String[]{"NIF", "Nombre", "Apellido", "Dirección", "Teléfono", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaClientes = new JTable(modelo);
        tablaClientes.setRowHeight(25);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        // Contenedor central con espacio superior
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Panel para controles sobre la tabla
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPanel.setBackground(Color.WHITE);
        buscarPanel.add(new JLabel("Buscar por:"));
        comboBuscar = new JComboBox<>();
        txtBuscar = new JTextField(18);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> { txtBuscar.setText(""); cargarClientes();});
        buscarPanel.add(comboBuscar);
        buscarPanel.add(txtBuscar);
        buscarPanel.add(btnBuscar);
        buscarPanel.add(btnLimpiar);

        JPanel ordenPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ordenPanel.setBackground(Color.WHITE);
        ordenPanel.add(new JLabel("Ordenar por:"));
        comboOrdenar.addActionListener(e -> {
            int idx = comboOrdenar.getSelectedIndex();
            if (idx >= 0) ordenarPorColumna(idx);
        });
        ordenPanel.add(comboOrdenar);

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setBackground(Color.WHITE);
        topControls.add(buscarPanel, BorderLayout.WEST);
        topControls.add(ordenPanel, BorderLayout.EAST);

        header.add(topControls, BorderLayout.NORTH);
        header.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        contenedorCentral.add(header, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);

        cargarClientes();
    }

    private void cargarClientes() {
        modelo.setRowCount(0);
        for (Cliente c : clienteDAO.listarTodos()) {
            modelo.addRow(new Object[]{
                    c.getNif(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getDireccion(),
                    c.getTelefono(),
                    c.getEmail()
            });
        }
        poblarComboOrdenar();
        poblarComboBuscar();
    }

    private void poblarComboOrdenar() {
        comboOrdenar.removeAllItems();
        for (int i = 0; i < modelo.getColumnCount(); i++) {
            comboOrdenar.addItem(modelo.getColumnName(i));
        }
        comboOrdenar.setSelectedIndex(-1);
    }

    private void poblarComboBuscar() {
        comboBuscar.removeAllItems();
        for (int i = 0; i < modelo.getColumnCount(); i++) comboBuscar.addItem(modelo.getColumnName(i));
        comboBuscar.setSelectedIndex(-1);
    }

    private void ordenarPorColumna(int colIndex) {
        int rows = modelo.getRowCount();
        List<Object[]> datos = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            Object[] fila = new Object[modelo.getColumnCount()];
            for (int c = 0; c < fila.length; c++) fila[c] = modelo.getValueAt(r, c);
            datos.add(fila);
        }

        Comparator<Object[]> cmp = (a, b) -> {
            Object va = a[colIndex];
            Object vb = b[colIndex];
            if (va == null) va = "";
            if (vb == null) vb = "";
            try {
                Double da = Double.valueOf(va.toString());
                Double db = Double.valueOf(vb.toString());
                return da.compareTo(db);
            } catch (Exception ex) {
                return va.toString().toLowerCase().compareTo(vb.toString().toLowerCase());
            }
        };

        datos.sort(cmp);
        modelo.setRowCount(0);
        for (Object[] f : datos) modelo.addRow(f);
    }

    private void cargarSeleccion() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) return;

        txtNif.setText((String) modelo.getValueAt(fila, 0));
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtApellido.setText((String) modelo.getValueAt(fila, 2));
        txtDireccion.setText((String) modelo.getValueAt(fila, 3));
        txtTelefono.setText((String) modelo.getValueAt(fila, 4));
        txtEmail.setText((String) modelo.getValueAt(fila, 5));
    }

    private void guardarCliente() {
        try {
            Cliente c = new Cliente(
                    txtNif.getText(),
                    txtNombre.getText(),
                    txtApellido.getText(),
                    txtDireccion.getText(),
                    txtTelefono.getText(),
                    txtEmail.getText()
            );

            clienteDAO.guardar(c);
            cargarClientes();
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos");
        }
    }

    private void modificarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente");
            return;
        }

        Cliente c = clienteDAO.buscarPorNif((String) modelo.getValueAt(fila, 0));

        try {
            c.setNombre(txtNombre.getText());
            c.setApellido(txtApellido.getText());
            c.setDireccion(txtDireccion.getText());
            c.setTelefono(txtTelefono.getText());
            c.setEmail(txtEmail.getText());

            clienteDAO.actualizar(c);
            cargarClientes();
            JOptionPane.showMessageDialog(this, "Cliente modificado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos");
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente");
            return;
        }

        String nif = (String) modelo.getValueAt(fila, 0);
        Cliente c = clienteDAO.buscarPorNif(nif);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres eliminar el cliente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        clienteDAO.eliminar(c);
        cargarClientes();
        JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
    }

    private void buscarClientes() {
        int col = comboBuscar.getSelectedIndex();
        String q = txtBuscar.getText();
        if (col < 0 || q == null || q.isBlank()) {
            cargarClientes();
            return;
        }
        q = q.toLowerCase();
        modelo.setRowCount(0);
        for (Cliente c : clienteDAO.listarTodos()) {
            Object[] row = new Object[]{
                    c.getNif(), c.getNombre(), c.getApellido(), c.getDireccion(), c.getTelefono(), c.getEmail()
            };
            Object field = row[col];
            String s = field == null ? "" : field.toString().toLowerCase();
            if (s.contains(q)) modelo.addRow(row);
        }
    }

    // Métodos de import/export JSON
    private void exportarClientesJson() {
        JFileChooser fc = new JFileChooser();
        int sel = fc.showSaveDialog(this);
        if (sel != JFileChooser.APPROVE_OPTION) return;
        java.io.File f = fc.getSelectedFile();
        try {
            org.facturacion.io.ExportImportService svc = new org.facturacion.io.ExportImportService();
            svc.exportClientesToJson(f);
            JOptionPane.showMessageDialog(this, "Exportación completada: " + f.getAbsolutePath());
        } catch (org.facturacion.io.ImportExportException ex) {
            JOptionPane.showMessageDialog(this, "Error en exportación: " + ex.getMessage());
        }
    }

    private void importarClientesJson() {
        JFileChooser fc = new JFileChooser();
        int sel = fc.showOpenDialog(this);
        if (sel != JFileChooser.APPROVE_OPTION) return;
        java.io.File f = fc.getSelectedFile();
        int opcion = JOptionPane.showConfirmDialog(this, "¿Actualizar registros existentes si coincide NIF?\n(Si no, sólo se añadirán nuevos)", "Modo importación", JOptionPane.YES_NO_OPTION);
        boolean upsert = opcion == JOptionPane.YES_OPTION;
        try {
            org.facturacion.io.ExportImportService svc = new org.facturacion.io.ExportImportService();
            int imported = svc.importClientesFromJson(f, upsert);
            cargarClientes();
            JOptionPane.showMessageDialog(this, "Importación completada. Registros procesados: " + imported);
        } catch (org.facturacion.io.ImportExportException ex) {
            JOptionPane.showMessageDialog(this, "Error en importación: " + ex.getMessage());
        }
    }
}
