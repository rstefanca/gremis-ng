package cz.upce.gremis.gremisng.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Embeddable
@Data
@Setter(value = AccessLevel.PRIVATE)
public class AuditAttributes {

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date changed;

    @Column(name = "CREATED_BY", updatable = false)
    //@CreatedBy
    private Long createdBy;

    @Column(name = "CHANGED_BY")
    //@LastModifiedBy
    private Long changedBy;
}
