package org.facturacion;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import org.facturacion.ui.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vp = new VentanaPrincipal();
            vp.setVisible(true);
        });
    }
}
