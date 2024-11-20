package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private final Connection connection;

    public JdbcTemplate(final Connection connection) {
        this.connection = connection;
    }

    public void execute(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeUpdate(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, rowMapper);
        if (results.size() != 1) {
            throw new RuntimeException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long insertAndReturnGeneratedKey(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            return extractGeneratedKey(statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long extractGeneratedKey(Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            return extractGeneratedKey(resultSet);
        }
    }

    private long extractGeneratedKey(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getLong(1);
        }

        throw new RuntimeException("generated key를 가져오는데 실패했습니다.");
    }
}
