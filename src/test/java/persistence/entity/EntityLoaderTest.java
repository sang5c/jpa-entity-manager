package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.domain.Person;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityLoaderTest extends DatabaseTest {

    private EntityLoader entityLoader;

    @Override
    protected List<String> getTableNames() {
        return List.of("my_users");
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        createTable(Person.class);
        entityLoader = new EntityLoader(jdbcTemplate);
    }

    @DisplayName("데이터베이스에서 Entity를 로드한다")
    @Test
    void loadEntity() {
        insert(new Person("bob", 32, "test@email.com"));

        Person person = entityLoader.loadEntity(Person.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("bob");
            softly.assertThat(person.getAge()).isEqualTo(32);
            softly.assertThat(person.getEmail()).isEqualTo("test@email.com");
        });
    }
}
