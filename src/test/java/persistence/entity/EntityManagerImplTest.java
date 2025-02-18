package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.domain.Person;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityManagerImplTest extends DatabaseTest {

    @Override
    public List<String> getTableNames() {
        return List.of("my_users");
    }

    @DisplayName("데이터베이스에서 객체를 조회한다")
    @Test
    void find() {
        createTable(Person.class);
        insert(new Person("bob", 32, "test@email.com"));

        EntityManager entityManager = EntityManagerImpl.createDefault(jdbcTemplate);
        Person person = entityManager.find(Person.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("bob");
            softly.assertThat(person.getAge()).isEqualTo(32);
            softly.assertThat(person.getEmail()).isEqualTo("test@email.com");
        });
    }

    @DisplayName("객체를 데이터베이스에 저장한다")
    @Test
    void persist() {
        createTable(Person.class);

        EntityManager entityManager = EntityManagerImpl.createDefault(jdbcTemplate);
        entityManager.persist(new Person("bob", 32, "test@email.com"));

        Person savedPerson = jdbcTemplate.queryForObject("select * from my_users", new EntityRowMapper<>(EntityMetadata.from(Person.class)));

        assertSoftly(softly -> {
            softly.assertThat(savedPerson.getId()).isEqualTo(1L);
            softly.assertThat(savedPerson.getName()).isEqualTo("bob");
            softly.assertThat(savedPerson.getAge()).isEqualTo(32);
            softly.assertThat(savedPerson.getEmail()).isEqualTo("test@email.com");
        });
    }

    @DisplayName("객체를 데이터베이스에서 삭제한다")
    @Test
    void remove() {
        createTable(Person.class);
        insert(new Person("bob", 32, "test@email.com"));

        Person person = new Person(1L, "bob", 32, "test@email.com", 1);
        EntityManager entityManager = EntityManagerImpl.createDefault(jdbcTemplate);
        entityManager.remove(person);

        List<Person> users = jdbcTemplate.query("select * from my_users", new EntityRowMapper<>(EntityMetadata.from(Person.class)));

        assertThat(users).isEmpty();
    }

    @DisplayName("객체를 데이터베이스에서 수정한다")
    @Test
    void update() {
        createTable(Person.class);
        Person person = new Person("person", 32, "test@email.com");
        EntityManager entityManager = EntityManagerImpl.createDefault(jdbcTemplate);
        Person savedPerson = entityManager.persist(person);
        savedPerson.setAge(99);
        entityManager.merge(savedPerson);

        Person resultPerson = jdbcTemplate.queryForObject("select * from my_users", new EntityRowMapper<>(EntityMetadata.from(Person.class)));

        assertSoftly(softly -> {
            softly.assertThat(resultPerson.getId()).isEqualTo(1L);
            softly.assertThat(resultPerson.getName()).isEqualTo("person");
            softly.assertThat(resultPerson.getAge()).isEqualTo(99);
            softly.assertThat(resultPerson.getEmail()).isEqualTo("test@email.com");
        });
    }
}
