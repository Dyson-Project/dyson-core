package org.dyson.core.model;

/**
 * Marker interface for all value objects.
 * Immutable, shareable, thin & simple
 * Implementations of this interface are required to implement equals() and hashCode().
 */
public interface ValueObject extends DomainObject {
    boolean equals(Object o);

    int hashCode();
}
