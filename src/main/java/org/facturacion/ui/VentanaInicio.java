package org.facturacion.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaInicio extends JPanel {

    public VentanaInicio() {

        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titulo = new JLabel("Bienvenido al Sistema de Facturación");
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitulo = new JLabel("Selecciona una pestaña para comenzar");
        subtitulo.setFont(Estilos.FUENTE_NORMAL);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);
        add(subtitulo, BorderLayout.CENTER);
    }
}
