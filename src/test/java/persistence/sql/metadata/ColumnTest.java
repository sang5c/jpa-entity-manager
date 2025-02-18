package persistence.sql.metadata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.ddl.dialect.H2Dialect;
import persistence.sql.ddl.fixture.EntityWithColumn;
import persistence.sql.ddl.fixture.IdentityStrategy;
import persistence.sql.ddl.fixture.IncludeId;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ColumnTest {

    private final Dialect dialect = new H2Dialect();

    @DisplayName("필드를 받아 컬럼으로 변환한다")
    @Test
    void fromField() throws Exception {
        Field field = IncludeId.class.getDeclaredField("name");
        Column column = Column.from(field);

        assertSoftly(softly -> {
            softly.assertThat(column.getName()).isEqualTo("name");
            softly.assertThat(column.getSqlType(dialect)).isEqualTo("varchar(255)");
            softly.assertThat(column.primaryKey()).isFalse();
        });
    }

    @DisplayName("@Id annotation이 추가되면 not null 제약조건을 갖는다.")
    @Test
    void idField() throws Exception {
        Field field = IncludeId.class.getDeclaredField("id");
        Column column = Column.from(field);

        assertSoftly(softly -> {
            softly.assertThat(column.getName()).isEqualTo("id");
            softly.assertThat(column.getSqlType(dialect)).isEqualTo("bigint");
            softly.assertThat(column.primaryKey()).isTrue();
        });
    }

    @DisplayName("@GeneratedValue(strategy = GenerationType.IDENTITY) annotation이 추가되면 AUTO_INCREMENT 제약조건을 갖는다.")
    @Test
    void autoIncrementField() throws Exception {
        Field field = IdentityStrategy.class.getDeclaredField("id");
        Column autoIncrementColumn = Column.from(field);

        assertThat(autoIncrementColumn.options()).contains(ColumnOption.IDENTITY);
    }

    @DisplayName("nullable=false인 필드는 not null 제약조건을 갖는다.")
    @Test
    void notNullField() throws Exception {
        Field field = EntityWithColumn.class.getDeclaredField("notNullColumn");

        Column column = Column.from(field);

        assertThat(column.options()).contains(ColumnOption.NOT_NULL);
    }

    @DisplayName("Column에 options가 있는지 확인한다.")
    @Test
    void hasOptions() throws Exception {
        Field field = EntityWithColumn.class.getDeclaredField("withoutColumn");

        Column column = Column.from(field);

        assertThat(column.hasOptions()).isFalse();
    }

    @DisplayName("SqlOptions를 반환한다.")
    @Test
    void getSqlOptions() throws Exception {
        Field field = EntityWithColumn.class.getDeclaredField("notNullColumn");

        Column column = Column.from(field);

        assertThat(column.getSqlOptions(dialect)).containsExactly("not null");
    }
}
