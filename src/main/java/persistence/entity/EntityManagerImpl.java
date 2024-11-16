package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    private EntityManagerImpl(PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
    }

    public static EntityManager createDefault(JdbcTemplate jdbcTemplate) {
        return new EntityManagerImpl(
                new PersistenceContextImpl(),
                EntityPersister.createDefault(jdbcTemplate)
        );
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        if (persistenceContext.contains(entityKey)) {
            return clazz.cast(persistenceContext.get(entityKey));
        }

        T entity = entityPersister.find(clazz, id);
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Long generatedKey = entityPersister.insert(entity);
        EntityKey entityKey = new EntityKey(entity.getClass(), generatedKey);
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), entityPersister.getIdValue(entity).value());
        persistenceContext.remove(entityKey);
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), entityPersister.getIdValue(entity).value());
        EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (entitySnapshot.isDirty(entity)) {
            entityPersister.update(entity);
        }

        persistenceContext.put(entityKey, entity);
        return entity;
    }
}
