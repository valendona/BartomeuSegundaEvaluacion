package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.Factura;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FacturaDAO {

    public void guardar(Factura factura) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(factura);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Factura> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Factura", Factura.class).list();
        }
    }

    public Factura buscarPorId(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Factura.class, id);
        }
    }

    public void eliminar(Factura factura) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(factura);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
