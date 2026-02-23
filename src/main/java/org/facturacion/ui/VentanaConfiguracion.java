package org.facturacion.ui;

import org.facturacion.dao.ConfiguracionDAO;

import javax.swing.*;
import java.awt.*;

public class VentanaConfiguracion extends JPanel {

    private final JTextField txtIVA;
    private final ConfiguracionDAO configDAO = new ConfiguracionDAO();

    public VentanaConfiguracion() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("IVA global (%):"));

        txtIVA = new JTextField(String.valueOf(configDAO.getIVA()));
        txtIVA.setPreferredSize(new Dimension(100, 28));
        txtIVA.setMaximumSize(new Dimension(180, 28)); // evita que crezca demasiado

        panel.add(txtIVA);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarIVA());

        add(panel, BorderLayout.NORTH);
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private void guardarIVA() {
        try {
            double nuevoIVA = Double.parseDouble(txtIVA.getText());

            if (nuevoIVA < 0 || nuevoIVA > 100) {
                JOptionPane.showMessageDialog(this, "El IVA debe estar entre 0 y 100");
                return;
            }

            configDAO.setIVA(nuevoIVA);
            JOptionPane.showMessageDialog(this, "IVA actualizado correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido");
        }
    }
}
