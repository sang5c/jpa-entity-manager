package persistence.entity;

public interface PersistenceContext {
    void put(EntityKey<?> entityKey, Object entity);

    boolean contains(EntityKey<?> entityKey);

    <T> T get(EntityKey<T> entityKey);

    void remove(EntityKey<?> entityKey);
}
