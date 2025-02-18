package persistence.sql.metadata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import persistence.sql.ddl.fixture.EntityWithColumn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnNameTest {

    @DisplayName("컬럼 이름은 비어있을 수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmptyName(String name) {
        assertThatThrownBy(() -> new ColumnName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("@Column을 포함하지 않은 경우 필드 이름 스네이크케이스가 컬럼 이름이 된다")
    @Test
    void notIncludeColumn() throws NoSuchFieldException {
        ColumnName columnName = ColumnName.from(EntityWithColumn.class.getDeclaredField("withoutColumn"));

        assertThat(columnName.value()).isEqualTo("without_column");
    }

    @DisplayName("@Column이 name을 포함한 경우 그 값이 컬럼 이름이 된다")
    @Test
    void includeColumn() throws NoSuchFieldException {
        ColumnName columnName = ColumnName.from(EntityWithColumn.class.getDeclaredField("withColumn"));

        assertThat(columnName.value()).isEqualTo("my_column");

    }

}
