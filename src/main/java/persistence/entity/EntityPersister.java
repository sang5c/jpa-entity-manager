package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.ColumnValue;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister<T> {
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    private final EntityMetadata<T> entityMetadata;
    private final EntityLoader entityLoader;
    private final JdbcTemplate jdbcTemplate;

    private EntityPersister(EntityMetadata<T> entityMetadata, EntityLoader entityLoader, JdbcTemplate jdbcTemplate) {
        this.entityMetadata = entityMetadata;
        this.entityLoader = entityLoader;
        this.jdbcTemplate = jdbcTemplate;
    }

    public static <T> EntityPersister<T> createDefault(Class<T> clazz, JdbcTemplate jdbcTemplate) {
        return new EntityPersister<>(
                EntityMetadata.from(clazz),
                new EntityLoader(jdbcTemplate),
                jdbcTemplate
        );
    }

    public boolean update(T entity) {
        try {
            String query = dmlQueryBuilder.buildUpdateQuery(entityMetadata, entity);
            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(T entity) {
        String query = dmlQueryBuilder.buildDeleteQuery(entityMetadata, entity);
        jdbcTemplate.execute(query);
    }

    public long insert(T entity) {
        String query = dmlQueryBuilder.buildInsertQuery(entityMetadata, entity);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        entityMetadata.fillId(entity, generatedKey);
        return generatedKey;
    }

    public ColumnValue getIdValue(T entity) {
        return entityMetadata.extractIdValue(entity);
    }

    public T find(Long id) {
        return entityLoader.loadEntity(entityMetadata, id);
    }
}
