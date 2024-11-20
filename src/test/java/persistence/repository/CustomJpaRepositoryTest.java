package persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.domain.Person;
import persistence.entity.EntityManagerImpl;
import persistence.entity.EntityRowMapper;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJpaRepositoryTest extends DatabaseTest {

    private CustomJpaRepository<Person> customJpaRepository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        createTable(Person.class);
        customJpaRepository = new CustomJpaRepository<>(EntityManagerImpl.createDefault(jdbcTemplate));
    }

    @DisplayName("새로운 엔티티를 저장하면 id가 생성된다")
    @Test
    void saveNew() {
        Person person = new Person("name", 20, "email");

        Person savedPerson = customJpaRepository.save(person);

        assertThat(savedPerson.getId()).isEqualTo(1L);
    }

    @DisplayName("기존 엔티티를 변경한 후 저장하면 변경된 값이 반영된다")
    @Test
    void saveExisting() {
        Person person = new Person("name", 20, "email");
        Person savedPerson = customJpaRepository.save(person);

        savedPerson.setName("new name");
        savedPerson.setAge(30);
        savedPerson.setEmail("new email");

        customJpaRepository.save(savedPerson);
        List<Person> persons = jdbcTemplate.query("select * from my_users", new EntityRowMapper<>(EntityMetadata.from(Person.class)));

        assertThat(persons).hasSize(1);
        assertThat(persons.get(0).getId()).isEqualTo(1L);
        assertThat(persons.get(0).getName()).isEqualTo("new name");
        assertThat(persons.get(0).getAge()).isEqualTo(30);
        assertThat(persons.get(0).getEmail()).isEqualTo("new email");
    }

    @Override
    protected List<String> getTableNames() {
        return List.of("my_users");
    }
}
