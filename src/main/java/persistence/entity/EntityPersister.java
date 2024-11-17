package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.ColumnValue;
import persistence.sql.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class EntityPersister {

    private final EntityLoader entityLoader;
    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final Map<Class<?>, EntityMetadata> metadataCache = new HashMap<>();

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
        EntityMetadata metadata = getMetadata(entity);
        String query = dmlQueryBuilder.buildUpdateQuery(metadata, entity);
        return jdbcTemplate.executeUpdate(query);
    }

    public void delete(Object entity) {
        EntityMetadata metadata = getMetadata(entity);
        String query = dmlQueryBuilder.buildDeleteQuery(metadata, entity);
        jdbcTemplate.execute(query);
    }

    public EntityKey insert(Object entity) {
        EntityMetadata metadata = getMetadata(entity);
        String query = dmlQueryBuilder.buildInsertQuery(metadata, entity);
        long generatedKey = jdbcTemplate.insertAndReturnGeneratedKey(query);
        metadata.fillId(entity, generatedKey);

        return new EntityKey(entity.getClass(), generatedKey);
    }

    public ColumnValue getIdValue(Object entity) {
        EntityMetadata metadata = getMetadata(entity);
        return metadata.extractIdValue(entity);
    }

    public <T> T find(Class<T> clazz, Long id) {
        EntityMetadata metadata = getMetadata(clazz);
        return entityLoader.loadEntity(metadata, id);
    }

    private EntityMetadata getMetadata(Class<?> entityClass) {
        return metadataCache.computeIfAbsent(entityClass, EntityMetadata::from);
    }

    private EntityMetadata getMetadata(Object entity) {
        return metadataCache.computeIfAbsent(entity.getClass(), EntityMetadata::from);
    }
}
