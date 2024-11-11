package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.domain.Person;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest extends DatabaseTest {

    private EntityPersister entityPersister;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        entityPersister = new EntityPersister(Person.class, jdbcTemplate);
        createTable(Person.class);
    }

    @Override
    protected List<String> getTableNames() {
        return List.of("my_users");
    }

    @DisplayName("insert를 수행하면 자동 증가된 키를 반환한다")
    @Test
    void insert() {
        Person person = new Person("bob", 32, "test@email.com");

        long key = entityPersister.insert(person);

        assertThat(key).isEqualTo(1L);
    }

    @DisplayName("update를 수행하면 데이터베이스에 반영된다")
    @Test
    void update() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        insert(person);
        person.setName("alice");

        entityPersister.update(person);
        Person updatedPerson = jdbcTemplate.queryForObject("select * from my_users where id = 1", new DefaultRowMapper<>(Person.class));

        assertThat(updatedPerson.getName()).isEqualTo("alice");
    }

    @DisplayName("delete를 수행하면 데이터베이스에서 삭제된다")
    @Test
    void delete() {
        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        insert(person);

        entityPersister.delete(person);
        List<Person> persons = jdbcTemplate.query("select * from my_users", new DefaultRowMapper<>(Person.class));

        assertThat(persons).isEmpty();
    }
}
