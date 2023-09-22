package org.dyson.core.model.event;

import org.dyson.core.model.DomainObject;

/**
 * Interface for all domain events.
 * This typically contains some metadata about the event, such as the date and time of the event,
 * but it may be a marker interface as well.
 */
public interface DomainEvent extends DomainObject {
}
