package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey<?>, Object> entityMap = new HashMap<>();

    @Override
    public void put(EntityKey<?> entityKey, Object entity) {
        entityMap.put(entityKey, entity);
    }

    @Override
    public boolean contains(EntityKey<?> entityKey) {
        return entityMap.containsKey(entityKey);
    }

    @Override
    public <T> T get(EntityKey<T> entityKey) {
        return entityKey.clazz().cast(entityMap.get(entityKey));
    }

    @Override
    public void remove(EntityKey<?> entityKey) {
        entityMap.remove(entityKey);
    }
}
