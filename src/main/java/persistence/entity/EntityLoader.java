package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityLoader(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public <T> T loadEntity(EntityMetadata metadata, Object id) {
        String selectQuery = dmlQueryBuilder.buildSelectByIdQuery(metadata, id);
        return jdbcTemplate.queryForObject(selectQuery, new EntityRowMapper<>(metadata));
    }
}
