package org.dyson.core.model;

import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
public abstract class LocalEntity<PK extends Serializable> extends AbstractEntity<PK> {
}
