package persistence.sql.metadata;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import persistence.entity.ColumnClause;
import persistence.sql.ddl.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public record Column(
        ColumnName name,
        String fieldName,
        Class<?> columnType,
        List<ColumnOption> options,
        boolean primaryKey
) {

    public static Column from(Field field) {
        return new Column(
                ColumnName.from(field),
                field.getName(),
                field.getType(),
                extractOptions(field),
                field.isAnnotationPresent(Id.class)
        );
    }

    private static List<ColumnOption> extractOptions(Field field) {
        List<ColumnOption> options = new ArrayList<>();

        if (hasNotNullOption(field)) {
            options.add(ColumnOption.NOT_NULL);
        }

        if (isIdentityStrategy(field)) {
            options.add(ColumnOption.IDENTITY);
        }

        return options;
    }

    private static boolean isIdentityStrategy(Field field) {
        return field.isAnnotationPresent(GeneratedValue.class)
                && field.getDeclaredAnnotation(GeneratedValue.class).strategy() == GenerationType.IDENTITY;
    }

    private static boolean hasNotNullOption(Field field) {
        return field.isAnnotationPresent(Id.class) || notNull(field);
    }

    private static boolean notNull(Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class)
                && !field.getDeclaredAnnotation(jakarta.persistence.Column.class).nullable();
    }

    public String getSqlType(Dialect dialect) {
        return dialect.getSqlType(columnType);
    }

    public String getName() {
        return name.value();
    }

    public List<String> getSqlOptions(Dialect dialect) {
        return options.stream()
                .map(dialect::getClause)
                .toList();
    }

    public boolean hasOptions() {
        return !options.isEmpty();
    }

    public boolean hasNotIdentityStrategy() {
        return !options.contains(ColumnOption.IDENTITY);
    }

    public ColumnValue extractColumnValue(Object entity) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return new ColumnValue(field.get(entity));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("필드를 찾을 수 없습니다: " + fieldName);
        }
    }

    public void fillValue(Object entity, Object value) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("필드를 찾을 수 없습니다: " + fieldName);
        }
    }

    public ColumnClause generateClause(Object entity) {
        return new ColumnClause(name, extractColumnValue(entity));
    }
}
