package cz.upce.gremis.gremisng.core;

public class EntityReference {
    private final Long id;
    private final Class<? extends AbstractEntity> entityClass;

    public EntityReference(Long id, Class<? extends AbstractEntity> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    public Long getId() {
        return id;
    }

    public Class<? extends AbstractEntity> getEntityClass() {
        return entityClass;
    }
}
