package persistence.sql.metadata;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnMetadata<T> {
    private final List<Column<T>> columns;

    private ColumnMetadata(List<Column<T>> columns) {
        validate(columns);
        this.columns = columns;
    }

    public static <T> ColumnMetadata<T> from(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(ColumnMetadata::isNotTransient)
                .map(Column::<T>from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ColumnMetadata::new));
    }

    private static boolean isNotTransient(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private void validate(List<Column<T>> columns) {
        boolean hasIdAnnotation = columns.stream()
                .anyMatch(Column::primaryKey);

        if (!hasIdAnnotation) {
            throw new IllegalArgumentException("@Id가 필수로 지정되어야 합니다");
        }
    }

    public List<Column<T>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public Column<T> getPrimaryKey() {
        return columns.stream()
                .filter(Column::primaryKey)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public void fillId(T entity, long generatedKey) {
        getPrimaryKey().fillValue(entity, generatedKey);
    }

    public ColumnValue extractIdValue(T entity) {
        return getPrimaryKey().extractColumnValue(entity);
    }

    public List<Column<T>> getColumnsWithoutPrimaryKey() {
        return columns.stream()
                .filter(Column::hasNotIdentityStrategy)
                .toList();
    }

    public void fillEntity(T entity, ResultSet resultSet) {
        columns.forEach(column -> column.fillValue(entity, getValue(resultSet, column)));
    }

    private Object getValue(ResultSet resultSet, Column<T> column) {
        try {
            return resultSet.getObject(column.getName(), column.columnType());
        } catch (SQLException e) {
            throw new IllegalArgumentException("ResultSet 값을 가져오는데 실패했습니다");
        }
    }
}
