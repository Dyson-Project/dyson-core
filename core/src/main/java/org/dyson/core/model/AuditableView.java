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
    public @Nullable String createdBy;
    public Instant createdDate;
    public @Nullable String lastModifiedBy;
    public Instant lastModifiedDate;
}
