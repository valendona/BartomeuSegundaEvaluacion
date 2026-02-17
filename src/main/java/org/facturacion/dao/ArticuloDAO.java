package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.Articulo;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ArticuloDAO {

    public void guardar(Articulo articulo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(articulo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void eliminar(Articulo articulo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(articulo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Articulo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Articulo.class, id);
        }
    }

    public List<Articulo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Articulo", Articulo.class).list();
        }
    }
}

