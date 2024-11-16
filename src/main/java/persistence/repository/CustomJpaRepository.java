package persistence.repository;

import persistence.entity.EntityManager;
import persistence.sql.metadata.EntityMetadata;

public class CustomJpaRepository<T> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }

    private boolean isNew(T entity) {
        EntityMetadata entityMetadata = EntityMetadata.from(entity.getClass());
        return entityMetadata.extractIdValue(entity).isNull();
    }
}
