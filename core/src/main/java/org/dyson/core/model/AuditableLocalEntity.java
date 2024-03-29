package org.dyson.core.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableLocalEntity<PK extends Serializable> extends LocalEntity<PK> implements Auditable {
    @CreatedBy
    public @Nullable String createdBy;

    public @Nullable Instant createdDate;

    @LastModifiedBy
    public @Nullable String lastModifiedBy;

    public @Nullable Instant lastModifiedDate;

    public void _created() {
        createdDate = Instant.now();
        lastModifiedDate = Instant.now();
    }

    public void _modified() {
        lastModifiedDate = Instant.now();
    }
}
