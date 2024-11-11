package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.Field;
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
        fillEntityFields(entity, resultSet);

        return entity;
    }

    private void fillEntityFields(T entity, ResultSet resultSet) {
        EntityMetadata.from(clazz)
                .getColumns()
                .forEach(column -> setField(resultSet, column, entity));
    }

    private void setField(ResultSet resultSet, Column column, T targetInstance) {
        try {
            Field field = clazz.getDeclaredField(column.fieldName());
            field.setAccessible(true);
            field.set(targetInstance, resultSet.getObject(column.getName(), column.columnType()));
        } catch (Exception e) {
            throw new IllegalStateException("필드 값 변경 실패", e);
        }
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
