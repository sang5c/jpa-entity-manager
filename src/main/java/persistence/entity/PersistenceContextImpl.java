package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> entityMap = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();

    @Override
    public void put(EntityKey entityKey, Object entity) {
        entityMap.put(entityKey, entity);
        entitySnapshots.put(entityKey, EntitySnapshot.from(entity));
    }

    @Override
    public boolean contains(EntityKey entityKey) {
        return entityMap.containsKey(entityKey);
    }

    @Override
    public Object get(EntityKey entityKey) {
        return entityMap.get(entityKey);
    }

    @Override
    public void remove(EntityKey entityKey) {
        entityMap.remove(entityKey);
        entitySnapshots.remove(entityKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(EntityKey entityKey) {
        return entitySnapshots.get(entityKey);
    }
}
