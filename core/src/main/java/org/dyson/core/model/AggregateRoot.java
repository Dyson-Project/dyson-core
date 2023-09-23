package org.dyson.core.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AggregateRoot<PK extends Serializable> extends AbstractEntity<PK> implements Auditable {

    @CreatedBy
    protected @Nullable String createdBy;

    protected @Nullable Instant createdDate;

    @LastModifiedBy
    protected @Nullable String lastModifiedBy;

    protected @Nullable Instant lastModifiedDate;

    public void _created() {
        createdDate = Instant.now();
        lastModifiedDate = Instant.now();
    }

    public void _modified() {
        lastModifiedDate = Instant.now();
    }
}
