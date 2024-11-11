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

    public <T> T loadEntity(Class<T> clazz, Object id) {
        String selectQuery = dmlQueryBuilder.buildSelectByIdQuery(EntityMetadata.from(clazz), id);
        return jdbcTemplate.queryForObject(selectQuery, new EntityRowMapper<>(clazz));
    }
}
