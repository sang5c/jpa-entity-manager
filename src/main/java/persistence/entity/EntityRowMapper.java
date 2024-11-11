package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;

public class EntityRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T entity = getEntityInstance();
        EntityMetadata<T> metadata = EntityMetadata.from(clazz);
        metadata.fillEntity(entity, resultSet);

        return entity;
    }

    private T getEntityInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException("인스턴스 생성에 실패했습니다", e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException("기본 생성자가 존재하지 않습니다.", e);
        }
    }
}
