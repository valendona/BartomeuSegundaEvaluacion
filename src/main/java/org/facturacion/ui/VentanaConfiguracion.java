package org.facturacion.ui;

import javax.swing.*;

public class VentanaConfiguracion extends JPanel {

    private static double iva = 21.0;

    public VentanaConfiguracion() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Estilos.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Configuración del Sistema");
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblIva = new JLabel("IVA (%):");
        lblIva.setFont(Estilos.FUENTE_NORMAL);
        lblIva.setAlignmentX(CENTER_ALIGNMENT);

        JTextField txtIva = new JTextField(String.valueOf(iva), 10);
        txtIva.setMaximumSize(txtIva.getPreferredSize());
        txtIva.setAlignmentX(CENTER_ALIGNMENT);

        BotonEstilizado btnGuardar = new BotonEstilizado("Guardar IVA");
        btnGuardar.setAlignmentX(CENTER_ALIGNMENT);

        btnGuardar.addActionListener(e -> {
            try {
                iva = Double.parseDouble(txtIva.getText());
                JOptionPane.showMessageDialog(this, "IVA actualizado correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Introduce un número válido");
            }
        });

        add(titulo);
        add(Box.createVerticalStrut(20));
        add(lblIva);
        add(txtIva);
        add(Box.createVerticalStrut(10));
        add(btnGuardar);
    }

    public static double getIVA() {
        return iva;
    }
}
