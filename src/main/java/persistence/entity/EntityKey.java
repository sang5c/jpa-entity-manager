package persistence.entity;

public record EntityKey(
        Class<?> clazz,
        Object id
) {
    public EntityKey {
        if (clazz == null) {
            throw new IllegalArgumentException("클래스 타입은 null이 될 수 없습니다.");
        }
        if (id == null) {
            throw new IllegalArgumentException("id는 null이 될 수 없습니다.");
        }
    }
}
