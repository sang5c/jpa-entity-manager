package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.metadata.EntityMetadata;

import java.sql.ResultSet;

public class EntityRowMapper<T> implements RowMapper<T> {

    private final EntityMetadata entityMetadata;

    public EntityRowMapper(EntityMetadata entityMetadata) {
        this.entityMetadata = entityMetadata;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        return entityMetadata.generateEntity(resultSet);
    }
}
