package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.domain.InsertPerson;
import persistence.sql.metadata.EntityMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class DmlQueryBuilderTest {

    private DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder();

    @DisplayName("객체를 이용하여 insert 쿼리를 생성한다")
    @Test
    void buildInsertQuery() {
        InsertPerson insertPerson = new InsertPerson(
                1L,
                "test",
                20,
                "test@email.com",
                1
        );

        String insertDml = dmlQueryBuilder.buildInsertQuery(EntityMetadata.from(InsertPerson.class), insertPerson);

        assertThat(insertDml).isEqualToIgnoringNewLines("insert into users (nick_name, old, email) values ('test', 20, 'test@email.com');");
    }

    @DisplayName("클래스 정보를 받아 select 쿼리를 생성한다")
    @Test
    void buildSelectAllQuery() {
        String selectDml = dmlQueryBuilder.buildSelectAllQuery(InsertPerson.class);

        assertThat(selectDml).isEqualToIgnoringNewLines("select id, nick_name, old, email from users;");
    }

    @DisplayName("클래스 정보와 id를 받아 select 쿼리를 생성한다")
    @Test
    void buildSelectAll() {
        String selectDml = dmlQueryBuilder.buildSelectByIdQuery(EntityMetadata.from(InsertPerson.class), 1L);

        assertThat(selectDml).isEqualToIgnoringNewLines("select id, nick_name, old, email from users where id = 1;");
    }

    @DisplayName("클래스 정보와 id를 받아 delete 쿼리를 생성한다")
    @Test
    void buildDeleteQuery() {
        InsertPerson insertPerson = new InsertPerson(1L, "test", 20, "test@email.com", 1);

        String deleteDml = dmlQueryBuilder.buildDeleteQuery(EntityMetadata.from(InsertPerson.class), insertPerson);

        assertThat(deleteDml).isEqualToIgnoringNewLines("delete from users where id = 1;");
    }
}
