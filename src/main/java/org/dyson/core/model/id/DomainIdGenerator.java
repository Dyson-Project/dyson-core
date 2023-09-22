package org.dyson.core.model.id;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerationStrategy;
import org.hibernate.id.uuid.StandardRandomStrategy;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.UUID;

@Slf4j
public class DomainIdGenerator implements IdentifierGenerator {
    private UUIDGenerationStrategy strategy;
    private Class<?> domainId;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        boolean isDomainId = false;
        domainId = type.getReturnedClass();
        for (Constructor<?> constructor : domainId.getConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 1 && types[0].equals(UUID.class)) {
                isDomainId = true;
                break;
            }
        }
        if (!isDomainId) {
            throw new IllegalCallerException(domainId + "must have at constructor(UUID id)");
        }
        strategy = StandardRandomStrategy.INSTANCE;

    }

    @SneakyThrows
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        UUID uuid = strategy.generateUUID(session);
        return domainId.getConstructor(UUID.class).newInstance(uuid);
    }
}
