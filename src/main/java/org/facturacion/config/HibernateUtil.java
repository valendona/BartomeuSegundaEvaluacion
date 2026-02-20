package org.facturacion.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Intentar cargar la configuración estándar (hibernate.cfg.xml)
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");

            // Registrar entidades anotadas por si faltan en el cfg
            cfg.addAnnotatedClass(org.facturacion.model.Articulo.class);
            cfg.addAnnotatedClass(org.facturacion.model.Cliente.class);
            cfg.addAnnotatedClass(org.facturacion.model.Albaran.class);
            cfg.addAnnotatedClass(org.facturacion.model.LineaAlbaran.class);
            cfg.addAnnotatedClass(org.facturacion.model.Factura.class);
            cfg.addAnnotatedClass(org.facturacion.model.LineaFactura.class);
            cfg.addAnnotatedClass(org.facturacion.model.Configuracion.class);

            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("No se pudo inicializar SessionFactory desde hibernate.cfg.xml: " + ex.getMessage());
            System.err.println("Intentando fallback a H2 embebida...");
            try {
                Configuration cfg = new Configuration();

                // Añadir propiedades para H2 file-based
                Properties props = new Properties();
                props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
                props.put("hibernate.connection.driver_class", "org.h2.Driver");
                props.put("hibernate.connection.url", "jdbc:h2:./data/facturacion_db;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE");
                props.put("hibernate.connection.username", "sa");
                props.put("hibernate.connection.password", "");
                props.put("hibernate.hbm2ddl.auto", "update");
                props.put("hibernate.show_sql", "false");

                cfg.setProperties(props);

                // Registrar entidades anotadas para el fallback
                cfg.addAnnotatedClass(org.facturacion.model.Articulo.class);
                cfg.addAnnotatedClass(org.facturacion.model.Cliente.class);
                cfg.addAnnotatedClass(org.facturacion.model.Albaran.class);
                cfg.addAnnotatedClass(org.facturacion.model.LineaAlbaran.class);
                cfg.addAnnotatedClass(org.facturacion.model.Factura.class);
                cfg.addAnnotatedClass(org.facturacion.model.LineaFactura.class);
                cfg.addAnnotatedClass(org.facturacion.model.Configuracion.class);

                return cfg.buildSessionFactory();
            } catch (Throwable e2) {
                System.err.println("Error creando SessionFactory de fallback (H2): " + e2.getMessage());
                throw new ExceptionInInitializerError(e2);
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
