package org.facturacion.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaPrincipal extends JFrame {

    private final JPanel panelContenido;
    private final JPanel panelInicio; // nuevo panel de inicio con accesos directos

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
        // INICIO (ACCESOS DIRECTOS)
        // -------------------------
        panelInicio = buildInicioPanel();

        // -------------------------
        // ACCIONES DE LOS BOTONES
        // -------------------------
        btnInicio.addActionListener(e -> mostrarPanel(panelInicio));
        btnClientes.addActionListener(e -> mostrarPanel(new VentanaClientes()));
        btnArticulos.addActionListener(e -> mostrarPanel(new VentanaArticulos()));
        btnAlbaranes.addActionListener(e -> mostrarPanel(new VentanaAlbaranes()));
        btnFacturas.addActionListener(e -> mostrarPanel(new VentanaFacturas()));
        btnConfig.addActionListener(e -> mostrarPanel(new VentanaConfiguracion()));

        // Mostrar inicio por defecto
        mostrarPanel(panelInicio);
    }

    private JPanel buildInicioPanel() {
        JPanel inicio = new JPanel(new BorderLayout());
        inicio.setBackground(Color.WHITE);
        inicio.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Bienvenido al Sistema de Facturación", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        inicio.add(titulo, BorderLayout.NORTH);

        // Usar dos filas centradas: topRow (3 tarjetas) y bottomRow (2 tarjetas)
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        topRow.setBackground(Color.WHITE);
        topRow.add(createShortcutButton("Clientes", "Gestiona clientes", e -> mostrarPanel(new VentanaClientes())));
        topRow.add(createShortcutButton("Artículos", "Gestiona artículos", e -> mostrarPanel(new VentanaArticulos())));
        topRow.add(createShortcutButton("Albaranes", "Gestiona albaranes", e -> mostrarPanel(new VentanaAlbaranes())));

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.add(createShortcutButton("Facturas", "Gestiona facturas", e -> mostrarPanel(new VentanaFacturas())));
        bottomRow.add(createShortcutButton("Configuración", "Ajustes del sistema", e -> mostrarPanel(new VentanaConfiguracion())));

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setBackground(Color.WHITE);
        rows.add(topRow);
        rows.add(Box.createVerticalStrut(10));
        rows.add(bottomRow);

        // Centrar las filas en el panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(rows);

        inicio.add(centerWrapper, BorderLayout.CENTER);

        // Pie con ayuda breve
        JLabel pie = new JLabel("Usa los accesos para navegar rápidamente", SwingConstants.CENTER);
        pie.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        inicio.add(pie, BorderLayout.SOUTH);

        return inicio;
    }

    private JComponent createShortcutButton(String title, String subtitle, java.awt.event.ActionListener action) {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        // Borde redondeado ligero para la tarjeta
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(220,220,220), 1, true));

        final Color primary = new Color(66, 133, 244);
        JButton boton = new JButton();
        boton.setFocusPainted(false);
        boton.setBackground(primary);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.setVerticalAlignment(SwingConstants.CENTER);
        // Borde redondeado para el botón que acompaña a la tarjeta
        boton.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1, true));
        // Aumentar tamaño de las tarjetas
        boton.setPreferredSize(new Dimension(250, 150));
        // Usa HTML para saltos de línea y estilo, centrado (fuentes más grandes)
        boton.setText("<html><div style='text-align:center'><span style='font-size:20px; font-weight:bold'>" + title + "</span><br><span style='font-size:14px;'>" + subtitle + "</span></div></html>");
        boton.addActionListener(action);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Mantener la tarjeta clicable pero SIN efectos visuales de hover
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boton.doClick();
            }
        });

        tarjeta.add(boton, new GridBagConstraints());
        return tarjeta;
    }

    private void mostrarPanel(JComponent panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
