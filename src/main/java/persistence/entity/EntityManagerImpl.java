package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityWrapper;

public class EntityManagerImpl<T> implements EntityManager<T> {

    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, PersistenceContext persistenceContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        EntityKey<T> entityKey = new EntityKey<>(clazz, id);
        if (persistenceContext.contains(entityKey)) {
            return persistenceContext.get(entityKey);
        }

        String query = dmlQueryBuilder.buildSelectByIdQuery(clazz, id);
        T entity = jdbcTemplate.queryForObject(query, new DefaultRowMapper<>(clazz));
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public T persist(T entity) {
        EntityWrapper entityWrapper = EntityWrapper.from(entity);
        String query = dmlQueryBuilder.buildInsertQuery(entityWrapper);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        entityWrapper.fillId(entity, generatedKey);

        persistenceContext.put(new EntityKey<>(entity.getClass(), generatedKey), entity);

        return entity;
    }

    @Override
    public void remove(T entity) {
        EntityWrapper entityWrapper = EntityWrapper.from(entity);
        persistenceContext.remove(new EntityKey<>(entity.getClass(), entityWrapper.getIdValue()));
        String query = dmlQueryBuilder.buildDeleteQuery(entityWrapper);
        jdbcTemplate.execute(query);
    }

    @Override
    public void update(T entity) {
        String query = dmlQueryBuilder.buildUpdateQuery(entity);
        jdbcTemplate.execute(query);
    }
}
