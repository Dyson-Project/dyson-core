package org.dyson.core.model.id;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dyson.core.model.ValueObject;

import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Danh cho hibernate
public class DomainStringId implements ValueObject, Serializable {
    protected String id;

    protected DomainStringId(@NotNull String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String unwrap() {
        return id;
    }

    // Sử dụng cho axon identifierConverter
    @Override
    public String toString() {
        return id;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DomainStringId)) {
            return false;
        }
        return Objects.equals(id, ((DomainStringId) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
