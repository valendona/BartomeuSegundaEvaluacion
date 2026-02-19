package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AlbaranDAO {

    public void guardar(org.facturacion.model.Albaran albaran) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(albaran);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    public org.facturacion.model.Albaran buscarPorId(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(org.facturacion.model.Albaran.class, id);
        }
    }

    public List<org.facturacion.model.Albaran> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Albaran", org.facturacion.model.Albaran.class).list();
        }
    }

    public void actualizar(org.facturacion.model.Albaran albaran) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(albaran);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    public void eliminar(org.facturacion.model.Albaran albaran) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(albaran) ? albaran : session.merge(albaran));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }
}
