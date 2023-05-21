package cz.upce.gremis.gremisng.core;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

@MappedSuperclass
@EntityListeners({EntityCreationListener.class})
public abstract class AbstractEntity implements Persistable<Long>, Serializable {

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @PostUpdate
    @PostPersist
    void postUpdate() {
        this.isNew = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        AbstractEntity that = (AbstractEntity) obj;

        return ObjectUtils.nullSafeEquals(this.getId(), that.getId());
    }
}
