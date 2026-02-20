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
        }
    }

    public void actualizar(Articulo articulo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(articulo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    public void eliminar(Articulo articulo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Comprobar referencias en LineaFactura
            Long cntFact = session.createQuery(
                    "select count(l) from LineaFactura l where l.articulo.codigo = :codigo",
                    Long.class
            ).setParameter("codigo", articulo.getCodigo()).uniqueResult();

            // Comprobar referencias en LineaAlbaran
            Long cntAlb = session.createQuery(
                    "select count(l) from LineaAlbaran l where l.articulo.codigo = :codigo",
                    Long.class
            ).setParameter("codigo", articulo.getCodigo()).uniqueResult();

            if ((cntFact != null && cntFact > 0) || (cntAlb != null && cntAlb > 0)) {
                throw new RuntimeException("No se puede eliminar el artículo: existen líneas de factura o albarán que lo referencian.");
            }

            session.remove(session.contains(articulo) ? articulo : session.merge(articulo));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error eliminando artículo", e);
        }
    }

    public Articulo buscarPorCodigo(String codigo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Articulo.class, codigo);
        }
    }

    public List<Articulo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Articulo", Articulo.class).list();
        }
    }
}
