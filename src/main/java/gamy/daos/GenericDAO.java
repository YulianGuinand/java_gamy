package gamy.daos;

import java.util.List;

public interface GenericDAO<T> {
    void create(T entity);
    T read(Long id);
    void update(T entity);
    void delete(T entity);
    List<T> findAll();
}