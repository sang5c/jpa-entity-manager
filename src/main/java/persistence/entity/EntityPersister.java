package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityWrapper;

public class EntityPersister {
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean update(Object entity) {
        try {
            String query = dmlQueryBuilder.buildUpdateQuery(entity);
            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(EntityWrapper entityWrapper) {
        String query = dmlQueryBuilder.buildDeleteQuery(entityWrapper);
        jdbcTemplate.execute(query);
    }

    public long insert(Object entity) {
        EntityWrapper entityWrapper = EntityWrapper.from(entity);
        String query = dmlQueryBuilder.buildInsertQuery(entityWrapper);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        entityWrapper.fillId(entity, generatedKey);
        return generatedKey;
    }
}
