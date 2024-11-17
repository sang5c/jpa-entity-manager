package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.ColumnValue;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister {

    private final EntityLoader entityLoader;
    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;

    private EntityPersister(EntityLoader entityLoader, JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.entityLoader = entityLoader;
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public static EntityPersister createDefault(JdbcTemplate jdbcTemplate) {
        DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

        return new EntityPersister(
                new EntityLoader(jdbcTemplate, dmlQueryBuilder),
                jdbcTemplate,
                dmlQueryBuilder
        );
    }

    public boolean update(Object entity) {
        EntityMetadata metadata = EntityMetadata.from(entity.getClass());
        String query = dmlQueryBuilder.buildUpdateQuery(metadata, entity);
        return jdbcTemplate.executeUpdate(query);
    }

    public void delete(Object entity) {
        EntityMetadata metadata = EntityMetadata.from(entity.getClass());
        String query = dmlQueryBuilder.buildDeleteQuery(metadata, entity);
        jdbcTemplate.execute(query);
    }

    public long insert(Object entity) {
        EntityMetadata metadata = EntityMetadata.from(entity.getClass());
        String query = dmlQueryBuilder.buildInsertQuery(metadata, entity);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        metadata.fillId(entity, generatedKey);
        return generatedKey;
    }

    public ColumnValue getIdValue(Object entity) {
        EntityMetadata metadata = EntityMetadata.from(entity.getClass());
        return metadata.extractIdValue(entity);
    }

    public <T> T find(Class<T> clazz, Long id) {
        EntityMetadata metadata = EntityMetadata.from(clazz);
        return entityLoader.loadEntity(metadata, id);
    }
}
