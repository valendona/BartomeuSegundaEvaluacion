package org.facturacion.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonEstilizado extends JButton {

    public BotonEstilizado(String texto) {
        super(texto);

        setBackground(Estilos.COLOR_BOTON);
        setForeground(Estilos.COLOR_TEXTO_BOTON);
        setFont(Estilos.FUENTE_BOTON);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Estilos.COLOR_BOTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Estilos.COLOR_BOTON);
            }
        });
    }
}
