package persistence.sql.metadata;

import java.sql.ResultSet;
import java.util.List;

public class EntityMetadata<T> {

    private final TableName tableName;
    private final ColumnMetadata<T> columnMetadata;

    private EntityMetadata(TableName tableName, ColumnMetadata<T> columnMetadata) {
        this.tableName = tableName;
        this.columnMetadata = columnMetadata;
    }

    public static <T> EntityMetadata<T> from(Class<T> clazz) {
        TableName tableName = TableName.from(clazz);
        ColumnMetadata<T> columnMetadata = ColumnMetadata.from(clazz);
        return new EntityMetadata<>(tableName, columnMetadata);
    }

    public String getTableName() {
        return tableName.value();
    }

    public String getPrimaryKeyName() {
        return columnMetadata.getPrimaryKey().getName();
    }

    public List<Column<T>> getColumns() {
        return columnMetadata.getColumns();
    }

    public List<Column<T>> getColumnsWithoutAutoGenerated() {
        return columnMetadata.getColumnsWithoutPrimaryKey();
    }

    public void fillId(T entity, long generatedKey) {
        columnMetadata.fillId(entity, generatedKey);
    }

    public ColumnValue extractIdValue(T entity) {
        return columnMetadata.extractIdValue(entity);
    }

    public Column<T> getPrimaryKey() {
        return columnMetadata.getPrimaryKey();
    }

    public void fillEntity(T entity, ResultSet resultSet) {
        columnMetadata.fillEntity(entity, resultSet);
    }
}
