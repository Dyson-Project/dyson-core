package org.dyson.core.model;

/**
 * Interface for all domain objects that can be uniquely identified in some context.
 * I often design this as a generic interface with the ID type as a generic parameter.
 */
public interface IdentifiableDomainObject<PK> extends DomainObject {
}
