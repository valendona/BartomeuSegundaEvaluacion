package org.facturacion.ui;

import org.facturacion.dao.ClienteDAO;
import org.facturacion.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaClientes extends JPanel {

    private JTextField txtDni, txtNombre, txtApellido;
    private JTable tabla;
    private DefaultTableModel modelo;

    private ClienteDAO clienteDAO = new ClienteDAO();

    public VentanaClientes() {

        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(4, 2, 10, 10));
        panelSuperior.setBackground(Estilos.COLOR_FONDO);

        panelSuperior.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelSuperior.add(txtDni);

        panelSuperior.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelSuperior.add(txtNombre);

        panelSuperior.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelSuperior.add(txtApellido);

        BotonEstilizado btnGuardar = new BotonEstilizado("Guardar Cliente");
        btnGuardar.addActionListener(e -> guardarCliente());
        panelSuperior.add(btnGuardar);

        add(panelSuperior, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"DNI", "Nombre", "Apellido"}, 0);
        tabla = crearTablaBonita(modelo);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargarClientes();
    }

    private JTable crearTablaBonita(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(Estilos.FUENTE_NORMAL);
        tabla.setRowHeight(25);

        tabla.getTableHeader().setFont(Estilos.FUENTE_BOTON);
        tabla.getTableHeader().setBackground(new Color(66, 133, 244));
        tabla.getTableHeader().setForeground(Color.WHITE);

        return tabla;
    }

    private void guardarCliente() {
        Cliente c = new Cliente(txtDni.getText(), txtNombre.getText(), txtApellido.getText());
        clienteDAO.guardar(c);
        cargarClientes();
    }

    private void cargarClientes() {
        modelo.setRowCount(0);
        for (Cliente c : clienteDAO.listarTodos()) {
            modelo.addRow(new Object[]{c.getDni(), c.getNombre(), c.getApellido()});
        }
    }
}
