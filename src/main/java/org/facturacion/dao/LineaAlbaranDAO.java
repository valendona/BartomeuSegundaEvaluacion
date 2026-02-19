package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.LineaAlbaran;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LineaAlbaranDAO {

    public void guardar(LineaAlbaran linea) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(linea);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
