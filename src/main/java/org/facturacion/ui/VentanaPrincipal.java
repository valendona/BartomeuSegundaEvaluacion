package org.facturacion.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {

        setTitle("Sistema de Facturación");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Aplicar fondo global
        getContentPane().setBackground(Estilos.COLOR_FONDO);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(Estilos.FUENTE_BOTON);

        pestañas.addTab("Inicio", new VentanaInicio());
        pestañas.addTab("Clientes", new VentanaClientes());
        pestañas.addTab("Artículos", new VentanaArticulos());
        pestañas.addTab("Facturas", new VentanaFacturas());
        pestañas.addTab("Configuración", new VentanaConfiguracion());

        add(pestañas, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.aplicarTemaGlobal();
            new VentanaPrincipal().setVisible(true);
        });
    }
}
