package org.dyson.core.model.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.Instant;


@Setter
@Getter
public abstract class ModifiedEvent implements DomainEvent {
    public @Nullable String lastModifiedBy;
    public @Nullable Instant lastModifiedDate;

    public ModifiedEvent(@Nullable String lastModifiedBy, @Nullable Instant lastModifiedDate) {
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }
}
