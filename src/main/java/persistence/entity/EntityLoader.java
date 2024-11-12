package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = new DmlQueryBuilder();
    }

    public <T> T loadEntity(EntityMetadata<T> metadata, Object id) {
        String selectQuery = dmlQueryBuilder.buildSelectByIdQuery(metadata, id);
        return jdbcTemplate.queryForObject(selectQuery, new EntityRowMapper<>(metadata.getEntityClass()));
    }
}
