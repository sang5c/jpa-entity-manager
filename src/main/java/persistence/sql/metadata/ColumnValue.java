package persistence.sql.metadata;

public record ColumnValue(
        Object value
) {

    public boolean isNull() {
        return value == null;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        }

        if (value instanceof Number) {
            return value.toString();
        }

        return "'" + value + "'";
    }
}
