package org.facturacion.dao;

import org.facturacion.config.HibernateUtil;
import org.facturacion.model.Factura;
import org.facturacion.model.LineaFactura;
import org.facturacion.model.Articulo;
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
        }
    }

    public void guardarConLineas(Factura factura, List<LineaFactura> lineas) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.persist(factura);

            for (LineaFactura lf : lineas) {
                // Asegurarse de trabajar con el artículo gestionado
                Articulo managedArticulo = session.get(Articulo.class, lf.getArticulo().getCodigo());
                if (managedArticulo == null) {
                    throw new RuntimeException("Artículo no encontrado: " + lf.getArticulo().getCodigo());
                }

                int nuevoStock = managedArticulo.getStock() - lf.getCantidad();
                if (nuevoStock < 0) {
                    throw new RuntimeException("Stock insuficiente para el artículo: " + managedArticulo.getCodigo());
                }

                managedArticulo.setStock(nuevoStock);
                session.merge(managedArticulo);

                lf.setArticulo(managedArticulo);
                lf.setFactura(factura);
                session.persist(lf);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Factura buscarPorId(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Usar fetch para inicializar las lineas y el artículo de cada linea
            Factura f = session.createQuery(
                    "select distinct fa from Factura fa left join fetch fa.lineas l left join fetch l.articulo where fa.id = :id",
                    Factura.class
            ).setParameter("id", id).uniqueResult();
            return f;
        }
    }

    public List<Factura> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Factura", Factura.class).list();
        }
    }

    public List<Factura> listarPorCliente(String nif) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Factura f where f.cliente.nif = :nif",
                    Factura.class
            ).setParameter("nif", nif).list();
        }
    }

    public void eliminar(Factura factura) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(factura) ? factura : session.merge(factura));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }
}
