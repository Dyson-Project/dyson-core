package org.dyson.core.model.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Setter
@Getter
public abstract class CreatedEvent implements DomainEvent {
    public @Nullable String createdBy;
    public @Nullable Instant createdDate;

    public CreatedEvent(@Nullable String createdBy, @Nullable Instant createdDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }
}
