package org.facturacion.ui;

import org.facturacion.dao.ClienteDAO;
import org.facturacion.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaClientes extends JPanel {

    private final JTextField txtNif, txtNombre, txtApellido, txtDireccion, txtTelefono, txtEmail;
    private final JTable tablaClientes;
    private final DefaultTableModel modelo;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public VentanaClientes() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------------------------------------------------------
        // FORMULARIO SUPERIOR
        // ---------------------------------------------------------
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

        // ESPACIO ENTRE FORMULARIO Y TABLA
        panelForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(panelForm, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // BOTONES INFERIORES
        // ---------------------------------------------------------
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

        add(panelBotones, BorderLayout.SOUTH);

        // ---------------------------------------------------------
        // TABLA DE CLIENTES
        // ---------------------------------------------------------
        modelo = new DefaultTableModel(
                new String[]{"NIF", "Nombre", "Apellido", "Dirección", "Teléfono", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaClientes = new JTable(modelo);
        tablaClientes.setRowHeight(25);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        // CONTENEDOR CENTRAL CON ESPACIO SUPERIOR
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contenedorCentral.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

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
}
