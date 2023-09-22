package org.dyson.core.model.id;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.dyson.core.model.ValueObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Danh cho hibernate
public abstract class DomainId implements ValueObject, Serializable {
    protected UUID id;

    protected DomainId(@NotNull UUID id) {
        this.id = id;
    }

    @SneakyThrows
    public static <T extends DomainId> T generateNewId(Class<T> clazz) {
        return clazz.getConstructor(UUID.class).newInstance(UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID unwrap() {
        return id;
    }


    // Sử dụng cho axon identifierConverter
    @Override
    public String toString() {
        return Objects.toString(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DomainId)) {
            return false;
        }
        return Objects.equals(id, ((DomainId) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
