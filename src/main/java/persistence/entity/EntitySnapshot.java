package persistence.entity;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.ColumnMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntitySnapshot {

    private final Map<Column, Object> columnSnapshots;

    private EntitySnapshot(Map<Column, Object> columnSnapshots) {
        this.columnSnapshots = columnSnapshots;
    }

    public static EntitySnapshot from(Object entity) {
        ColumnMetadata metadata = ColumnMetadata.from(entity.getClass());
        Map<Column, Object> columnSnapshots = metadata.getColumns().stream()
                .collect(Collectors.toMap(column -> column, column -> column.extractColumnValue(entity)));

        return new EntitySnapshot(columnSnapshots);
    }

    public boolean isDirty(Object entity) {
        return columnSnapshots.entrySet().stream()
                .anyMatch(entry -> hasDiffValue(entity, entry));
    }

    private boolean hasDiffValue(Object entity, Map.Entry<Column, Object> entry) {
        Column column = entry.getKey();
        Object snapshotValue = entry.getValue();
        Object entityValue = column.extractColumnValue(entity);
        return !snapshotValue.equals(entityValue);
    }

    public List<ColumnClause> diffColumns(Object entity) {
        return columnSnapshots.entrySet().stream()
                .filter(entry -> hasDiffValue(entity, entry))
                .map(Map.Entry::getKey)
                .map(column -> column.generateClause(entity))
                .toList();
    }
}
