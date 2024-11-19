package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.ColumnValue;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister {

    private final EntityLoader entityLoader;
    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final EntityMetadata entityMetadata;

    private EntityPersister(EntityLoader entityLoader, JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder, EntityMetadata entityMetadata) {
        this.entityLoader = entityLoader;
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.entityMetadata = entityMetadata;
    }

    public static EntityPersister createDefault(JdbcTemplate jdbcTemplate, Class<?> clazz) {
        DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

        return new EntityPersister(
                new EntityLoader(jdbcTemplate, dmlQueryBuilder),
                jdbcTemplate,
                dmlQueryBuilder,
                EntityMetadata.from(clazz)
        );
    }

    public boolean update(Object entity) {
        String query = dmlQueryBuilder.buildUpdateQuery(entityMetadata, entity);
        return jdbcTemplate.executeUpdate(query);
    }

    public void delete(Object entity) {
        String query = dmlQueryBuilder.buildDeleteQuery(entityMetadata, entity);
        jdbcTemplate.execute(query);
    }

    public EntityKey insert(Object entity) {
        String query = dmlQueryBuilder.buildInsertQuery(entityMetadata, entity);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        entityMetadata.fillId(entity, generatedKey);

        return new EntityKey(entity.getClass(), generatedKey);
    }

    public ColumnValue getIdValue(Object entity) {
        return entityMetadata.extractIdValue(entity);
    }

    public <T> T find(Long id) {
        return entityLoader.loadEntity(entityMetadata, id);
    }
}
