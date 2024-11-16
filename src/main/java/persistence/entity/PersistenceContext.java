package persistence.entity;

public interface PersistenceContext {
    void put(EntityKey entityKey, Object entity);

    boolean contains(EntityKey entityKey);

    Object get(EntityKey entityKey);

    void remove(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey);
}
