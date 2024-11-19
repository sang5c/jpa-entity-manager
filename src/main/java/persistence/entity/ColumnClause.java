package persistence.entity;

import persistence.sql.metadata.ColumnName;
import persistence.sql.metadata.ColumnValue;

public record ColumnClause(
        ColumnName name,
        ColumnValue value
) {

    public String equalityExpression() {
        return name.value() + " = " + value;
    }
}
