package org.facturacion;

import javax.imageio.ImageIO;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import org.facturacion.ui.VentanaPrincipal;

import java.awt.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vp = new VentanaPrincipal();
            // Fallback: si no se cargó iconos en VentanaPrincipal, intentar cargar un icono simple
            try (InputStream is = Main.class.getResourceAsStream("/iconoappFA.png")) {
                if (is != null) {
                    Image img = ImageIO.read(is);
                    if (img != null) vp.setIconImage(img);
                }
            } catch (Exception ex) {
                // ignore
            }
            vp.setVisible(true);
        });
    }
}
