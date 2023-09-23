package org.dyson.core.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableView<PK extends Serializable> implements StandardType<PK> {
    protected @Nullable String createdBy;
    protected Instant createdDate;
    protected @Nullable String lastModifiedBy;
    protected Instant lastModifiedDate;
}
