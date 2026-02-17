package org.facturacion.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class Theme {

    public static void aplicarTemaGlobal() {

        // Fuente global
        UIManager.put("Label.font", Estilos.FUENTE_NORMAL);
        UIManager.put("Button.font", Estilos.FUENTE_BOTON);
        UIManager.put("TextField.font", Estilos.FUENTE_NORMAL);
        UIManager.put("ComboBox.font", Estilos.FUENTE_NORMAL);
        UIManager.put("Table.font", Estilos.FUENTE_NORMAL);
        UIManager.put("TableHeader.font", Estilos.FUENTE_BOTON);

        // Fondo global
        UIManager.put("Panel.background", Estilos.COLOR_FONDO);
        UIManager.put("ScrollPane.background", Estilos.COLOR_FONDO);

        // Botones
        UIManager.put("Button.background", Estilos.COLOR_BOTON);
        UIManager.put("Button.foreground", Color.WHITE);

        // Tablas
        UIManager.put("Table.selectionBackground", new Color(200, 220, 255));
        UIManager.put("Table.selectionForeground", Color.BLACK);

        // Scrollbars modernos
        UIManager.put("ScrollBarUI", BasicScrollBarUI.class.getName());
    }
}
