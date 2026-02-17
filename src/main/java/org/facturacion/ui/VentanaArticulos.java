package org.facturacion.ui;

import org.facturacion.dao.ArticuloDAO;
import org.facturacion.model.Articulo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaArticulos extends JPanel {

    private JTextField txtNombre, txtPrecio;
    private JTable tabla;
    private DefaultTableModel modelo;

    private ArticuloDAO articuloDAO = new ArticuloDAO();

    public VentanaArticulos() {

        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 10, 10));
        panelSuperior.setBackground(Estilos.COLOR_FONDO);

        panelSuperior.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelSuperior.add(txtNombre);

        panelSuperior.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelSuperior.add(txtPrecio);

        BotonEstilizado btnGuardar = new BotonEstilizado("Guardar Artículo");
        btnGuardar.addActionListener(e -> guardarArticulo());
        panelSuperior.add(btnGuardar);

        add(panelSuperior, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Nombre", "Precio"}, 0);
        tabla = crearTablaBonita(modelo);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargarArticulos();
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

    private void guardarArticulo() {
        Articulo a = new Articulo(txtNombre.getText(), Double.parseDouble(txtPrecio.getText()));
        articuloDAO.guardar(a);
        cargarArticulos();
    }

    private void cargarArticulos() {
        modelo.setRowCount(0);
        for (Articulo a : articuloDAO.listarTodos()) {
            modelo.addRow(new Object[]{a.getNombre(), a.getPrecio()});
        }
    }
}
