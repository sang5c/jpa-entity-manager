package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextImplTest {

    private PersistenceContextImpl persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContextImpl();
    }

    @DisplayName("Persistence Context에 Entity를 추가한다")
    @Test
    void put() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntityKey entityKey = new EntityKey(Person.class, person.getId());

        persistenceContext.put(entityKey, person);
        Person actualPerson = (Person) persistenceContext.get(entityKey);

        assertThat(actualPerson.getId()).isEqualTo(person.getId());
    }

    @DisplayName("Persistence Context에서 Entity를 제거한다")
    @Test
    void remove() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntityKey entityKey = new EntityKey(Person.class, person.getId());
        persistenceContext.put(entityKey, person);

        persistenceContext.remove(entityKey);

        assertThat(persistenceContext.get(entityKey)).isNull();
    }

    @DisplayName("저장되지 않은 Entity를 조회하면 null을 반환한다")
    @Test
    void get() {
        EntityKey entityKey = new EntityKey(Person.class, 1L);

        Person actualPerson = (Person) persistenceContext.get(entityKey);

        assertThat(actualPerson).isNull();
    }

    @DisplayName("Entity가 저장되어 있는지 확인한다")
    @Test
    void contains() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntityKey entityKey = new EntityKey(Person.class, person.getId());
        persistenceContext.put(entityKey, person);

        EntitySnapshot databaseSnapshot = persistenceContext.getDatabaseSnapshot(new EntityKey(Person.class, person.getId()));
        assertThat(databaseSnapshot).isNotNull();

        assertThat(persistenceContext.contains(entityKey)).isTrue();
    }
}
