package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.ColumnValue;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister {
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetadata entityMetadata;

    public EntityPersister(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        this.entityMetadata = EntityMetadata.from(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object entity) {
        try {
            String query = dmlQueryBuilder.buildUpdateQuery(entityMetadata, entity);
            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(Object entity) {
        String query = dmlQueryBuilder.buildDeleteQuery(entityMetadata, entity);
        jdbcTemplate.execute(query);
    }

    public long insert(Object entity) {
        String query = dmlQueryBuilder.buildInsertQuery(entityMetadata, entity);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        entityMetadata.fillId(entity, generatedKey);
        return generatedKey;
    }

    public ColumnValue getIdValue(Object entity) {
        return entityMetadata.extractIdValue(entity);
    }
}
