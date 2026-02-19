package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.Configuracion;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ConfiguracionDAO {

    public double getIVA() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Configuracion c = session.get(Configuracion.class, "iva");
            return c != null ? c.getValor() : 21;
        }
    }

    public void setIVA(double nuevoIVA) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Configuracion c = new Configuracion("iva", nuevoIVA);
            session.merge(c);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
