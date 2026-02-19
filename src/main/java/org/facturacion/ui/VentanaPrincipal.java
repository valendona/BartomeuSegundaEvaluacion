package org.facturacion.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final JPanel panelContenido;

    public VentanaPrincipal() {

        setTitle("Sistema de Facturación");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // -------------------------
        // BARRA DE NAVEGACIÓN
        // -------------------------
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnInicio = new JButton("Inicio");
        JButton btnClientes = new JButton("Clientes");
        JButton btnArticulos = new JButton("Artículos");
        JButton btnAlbaranes = new JButton("Albaranes");
        JButton btnFacturas = new JButton("Facturas");
        JButton btnConfig = new JButton("Configuración");

        barra.add(btnInicio);
        barra.add(btnClientes);
        barra.add(btnArticulos);
        barra.add(btnAlbaranes);
        barra.add(btnFacturas);
        barra.add(btnConfig);

        add(barra, BorderLayout.NORTH);

        // -------------------------
        // PANEL CENTRAL
        // -------------------------
        panelContenido = new JPanel(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);

        // -------------------------
        // ACCIONES DE LOS BOTONES
        // -------------------------
        btnInicio.addActionListener(e -> mostrarPanel(new JLabel("Bienvenido", SwingConstants.CENTER)));
        btnClientes.addActionListener(e -> mostrarPanel(new VentanaClientes()));
        btnArticulos.addActionListener(e -> mostrarPanel(new VentanaArticulos()));
        btnAlbaranes.addActionListener(e -> mostrarPanel(new VentanaAlbaranes()));
        btnFacturas.addActionListener(e -> mostrarPanel(new VentanaFacturas()));
        btnConfig.addActionListener(e -> mostrarPanel(new VentanaConfiguracion()));

        // Mostrar inicio por defecto
        mostrarPanel(new JLabel("Bienvenido", SwingConstants.CENTER));
    }

    private void mostrarPanel(JComponent panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
