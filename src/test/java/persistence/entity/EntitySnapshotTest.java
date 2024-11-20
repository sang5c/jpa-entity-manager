package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.domain.Person;
import persistence.sql.metadata.ColumnName;
import persistence.sql.metadata.ColumnValue;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntitySnapshotTest {

    @DisplayName("엔티티의 변경 여부를 확인한다")
    @Test
    void isDirty() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntitySnapshot entitySnapshot = EntitySnapshot.from(person);
        person.setName("alice");

        boolean dirty = entitySnapshot.isDirty(person);

        assertThat(dirty).isTrue();
    }

    @DisplayName("엔티티에서 변경된 컬럼를 확인한다")
    @Test
    void diffColumns() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntitySnapshot entitySnapshot = EntitySnapshot.from(person);

        person.setName("alice");

        List<ColumnClause> columnClauses = entitySnapshot.diffColumns(person);

        assertThat(columnClauses).containsExactly(new ColumnClause(new ColumnName("nick_name"), new ColumnValue("alice")));
    }
}
