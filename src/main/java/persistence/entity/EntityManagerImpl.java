package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityManagerImpl<T> implements EntityManager<T> {

    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        EntityKey<T> entityKey = new EntityKey<>(clazz, id);
        if (persistenceContext.contains(entityKey)) {
            return persistenceContext.get(entityKey);
        }

        String query = dmlQueryBuilder.buildSelectByIdQuery(EntityMetadata.from(clazz), id);
        T entity = jdbcTemplate.queryForObject(query, new DefaultRowMapper<>(clazz));
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
