package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.domain.Person;

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
}
