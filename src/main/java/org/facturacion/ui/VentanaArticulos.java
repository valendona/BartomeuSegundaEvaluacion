package org.facturacion.ui;

import org.facturacion.dao.ArticuloDAO;
import org.facturacion.model.Articulo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaArticulos extends JPanel {

    private final JTextField txtCodigo, txtNombre, txtPrecio, txtStock;
    private final JTable tablaArticulos;
    private final DefaultTableModel modelo;
    private JComboBox<String> comboOrdenar;
    private JComboBox<String> comboBuscar;
    private JTextField txtBuscar;

    private final ArticuloDAO articuloDAO = new ArticuloDAO();

    public VentanaArticulos() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------------------------------------------------------
        // FORMULARIO SUPERIOR
        // ---------------------------------------------------------
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));
        panelForm.setBackground(Color.WHITE);

        panelForm.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelForm.add(txtCodigo);

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelForm.add(txtPrecio);

        panelForm.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelForm.add(txtStock);

        // ESPACIO ENTRE FORMULARIO Y TABLA
        panelForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(panelForm, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // BOTONES INFERIORES
        // ---------------------------------------------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarArticulo());
        panelBotones.add(btnGuardar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> modificarArticulo());
        panelBotones.add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarArticulo());
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        // ---------------------------------------------------------
        // TABLA DE ARTÍCULOS
        // ---------------------------------------------------------
        modelo = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaArticulos = new JTable(modelo);
        tablaArticulos.setRowHeight(25);

        tablaArticulos.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        // CONTENEDOR CENTRAL CON ESPACIO SUPERIOR
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPanel.setBackground(Color.WHITE);
        buscarPanel.add(new JLabel("Buscar por:"));
        comboBuscar = new JComboBox<>();
        txtBuscar = new JTextField(14);
        JButton btnBuscar = new JButton("Buscar"); btnBuscar.addActionListener(e -> buscarArticulos());
        JButton btnLimpiar = new JButton("Limpiar"); btnLimpiar.addActionListener(e -> { txtBuscar.setText(""); cargarArticulos(); });
        buscarPanel.add(comboBuscar); buscarPanel.add(txtBuscar); buscarPanel.add(btnBuscar); buscarPanel.add(btnLimpiar);

        JPanel ordenPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ordenPanel.setBackground(Color.WHITE);
        ordenPanel.add(new JLabel("Ordenar por:"));
        comboOrdenar = new JComboBox<>();
        comboOrdenar.addActionListener(e -> {int idx = comboOrdenar.getSelectedIndex(); if (idx>=0) ordenarPorColumna(idx);});
        ordenPanel.add(comboOrdenar);

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setBackground(Color.WHITE);
        topControls.add(buscarPanel, BorderLayout.WEST);
        topControls.add(ordenPanel, BorderLayout.EAST);

        header.add(topControls, BorderLayout.NORTH);
        header.add(new JScrollPane(tablaArticulos), BorderLayout.CENTER);
        contenedorCentral.add(header, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);

        cargarArticulos();
    }

    private void cargarArticulos() {
        modelo.setRowCount(0);
        for (Articulo a : articuloDAO.listarTodos()) {
            modelo.addRow(new Object[]{
                    a.getCodigo(),
                    a.getNombre(),
                    a.getPrecio(),
                    a.getStock()
            });
        }
        poblarComboOrdenar();
        poblarComboBuscar();
    }

    private void poblarComboOrdenar() {
        comboOrdenar.removeAllItems();
        for (int i=0;i<modelo.getColumnCount();i++) comboOrdenar.addItem(modelo.getColumnName(i));
        comboOrdenar.setSelectedIndex(-1);
    }

    private void poblarComboBuscar(){
        comboBuscar.removeAllItems();
        for (int i=0;i<modelo.getColumnCount();i++) comboBuscar.addItem(modelo.getColumnName(i));
        comboBuscar.setSelectedIndex(-1);
    }

    private void ordenarPorColumna(int colIndex) {
        int rows = modelo.getRowCount();
        java.util.List<Object[]> datos = new java.util.ArrayList<>();
        for (int r=0;r<rows;r++){
            Object[] fila = new Object[modelo.getColumnCount()];
            for (int c=0;c<fila.length;c++) fila[c]=modelo.getValueAt(r,c);
            datos.add(fila);
        }
        java.util.Comparator<Object[]> cmp = (a,b)->{
            Object va=a[colIndex], vb=b[colIndex]; if (va==null) va=""; if (vb==null) vb="";
            try{ Double da=Double.valueOf(va.toString()); Double db=Double.valueOf(vb.toString()); return da.compareTo(db);}catch(Exception ex){return va.toString().toLowerCase().compareTo(vb.toString().toLowerCase());}
        };
        datos.sort(cmp);
        modelo.setRowCount(0);
        for (Object[] f:datos) modelo.addRow(f);
    }

    private void cargarSeleccion() {
        int fila = tablaArticulos.getSelectedRow();
        if (fila == -1) return;

        txtCodigo.setText((String) modelo.getValueAt(fila, 0));
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtPrecio.setText(String.valueOf(modelo.getValueAt(fila, 2)));
        txtStock.setText(String.valueOf(modelo.getValueAt(fila, 3)));
    }

    private void guardarArticulo() {
        try {
            Articulo a = new Articulo(
                    txtNombre.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText())
            );

            articuloDAO.guardar(a);
            cargarArticulos();
            JOptionPane.showMessageDialog(this, "Artículo guardado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos");
        }
    }

    private void modificarArticulo() {
        int fila = tablaArticulos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo");
            return;
        }

        Articulo a = articuloDAO.buscarPorCodigo((String) modelo.getValueAt(fila, 0));

        try {
            a.setNombre(txtNombre.getText());
            a.setPrecio(Double.parseDouble(txtPrecio.getText()));
            a.setStock(Integer.parseInt(txtStock.getText()));

            articuloDAO.actualizar(a);
            cargarArticulos();
            JOptionPane.showMessageDialog(this, "Artículo modificado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos");
        }
    }

    private void eliminarArticulo() {
        int fila = tablaArticulos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo");
            return;
        }

        String codigo = (String) modelo.getValueAt(fila, 0);
        Articulo a = articuloDAO.buscarPorCodigo(codigo);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres eliminar el artículo?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        articuloDAO.eliminar(a);
        cargarArticulos();
        JOptionPane.showMessageDialog(this, "Artículo eliminado correctamente");
    }

    private void buscarArticulos(){
        int col = comboBuscar.getSelectedIndex();
        String q = txtBuscar.getText();
        if (col<0 || q==null || q.isBlank()){ cargarArticulos(); return; }
        q = q.toLowerCase(); modelo.setRowCount(0);
        for (Articulo a: articuloDAO.listarTodos()){
            Object[] row = new Object[]{ a.getCodigo(), a.getNombre(), a.getPrecio(), a.getStock() };
            Object field = row[col]; String s = field==null?"":field.toString().toLowerCase(); if (s.contains(q)) modelo.addRow(row);
        }
    }
}
