package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.LineaFactura;
import org.facturacion.model.Articulo;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LineaFacturaDAO {

    public void guardar(LineaFactura linea) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Asegurarse de trabajar con instancias gestionadas
            if (linea.getArticulo() != null) {
                Articulo managedArticulo = session.get(Articulo.class, linea.getArticulo().getCodigo());
                if (managedArticulo == null) {
                    throw new RuntimeException("Artículo no encontrado: " + linea.getArticulo().getCodigo());
                }

                int nuevoStock = managedArticulo.getStock() - linea.getCantidad();
                if (nuevoStock < 0) {
                    throw new RuntimeException("Stock insuficiente para el artículo: " + managedArticulo.getCodigo());
                }

                managedArticulo.setStock(nuevoStock);
                session.merge(managedArticulo);

                // asegurar que la linea apunte al artículo gestionado
                linea.setArticulo(managedArticulo);
            }

            session.persist(linea);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
