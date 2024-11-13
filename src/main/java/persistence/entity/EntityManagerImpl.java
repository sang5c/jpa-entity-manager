package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl<T> implements EntityManager<T> {
    private final PersistenceContext persistenceContext;
    private final EntityPersister<T> entityPersister;

    private EntityManagerImpl(PersistenceContext persistenceContext, EntityPersister<T> entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
    }

    public static <T> EntityManager<T> createDefault(Class<T> clazz, JdbcTemplate jdbcTemplate) {
        return new EntityManagerImpl<>(
                new PersistenceContextImpl(),
                EntityPersister.createDefault(clazz, jdbcTemplate)
        );
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        EntityKey<T> entityKey = new EntityKey<>(clazz, id);
        if (persistenceContext.contains(entityKey)) {
            return persistenceContext.get(entityKey);
        }

        T entity = entityPersister.find(id);
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public T persist(T entity) {
        long generatedKey = entityPersister.insert(entity);
        persistenceContext.put(new EntityKey<>(entity.getClass(), generatedKey), entity);

        return entity;
    }

    @Override
    public void remove(T entity) {
        persistenceContext.remove(new EntityKey<>(entity.getClass(), entityPersister.getIdValue(entity)));
        entityPersister.delete(entity);
    }

    @Override
    public void update(T entity) {
        entityPersister.update(entity);
    }
}
