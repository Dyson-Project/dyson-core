package org.dyson.core.modeling.command;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.lock.LockFactory;
import org.axonframework.common.lock.NullLockFactory;
import org.axonframework.eventhandling.DomainEventSequenceAware;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.annotation.HandlerDefinition;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.modelling.command.LockingRepository;
import org.axonframework.modelling.command.RepositoryProvider;
import org.axonframework.modelling.command.inspection.AggregateModel;
import org.axonframework.modelling.command.inspection.AnnotatedAggregate;
import org.axonframework.tracing.SpanFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.axonframework.common.BuilderUtils.assertNonNull;

@Slf4j
public class AggregateRepository<T> extends LockingRepository<T, AnnotatedAggregate<T>> {
    private final EntityManagerProvider entityManagerProvider;
    private final EventBus eventBus;
    private final RepositoryProvider repositoryProvider;
    private final Function<String, ?> identifierConverter;
    private final boolean generateSequenceNumbers;

    private boolean forceFlushOnSave = true;

    /**
     * Instantiate a {@link AggregateRepository} based on the fields contained in the {@link Builder}.
     * <p>
     * A goal of the provided Builder is to create an {@link AggregateModel} specifying generic {@code T} as the
     * aggregate type to be stored. All aggregates in this repository must be {@code instanceOf} this aggregate type.
     * To instantiate this AggregateModel, either an {@link AggregateModel} can be provided directly or an
     * {@code aggregateType} of type {@link Class} can be used. The latter will internally resolve to an
     * AggregateModel. Thus, either the AggregateModel <b>or</b> the {@code aggregateType} should be provided. An
     * {@link org.axonframework.common.AxonConfigurationException} is thrown if this criteria is not met.
     * <p>
     * Additionally will assert that the {@link LockFactory}, {@link EntityManagerProvider}, {@link EventBus} and
     * {@code identifierConverter} are not {@code null}, resulting in an AxonConfigurationException if for any of these
     * this is the case.
     *
     * @param builder the {@link Builder} used to instantiate a {@link AggregateRepository} instance
     */
    protected AggregateRepository(Builder<T> builder) {
        super(builder);
        this.entityManagerProvider = builder.entityManagerProvider;
        this.eventBus = builder.eventBus;
        this.identifierConverter = builder.identifierConverter;
        this.repositoryProvider = builder.repositoryProvider;
        this.generateSequenceNumbers = builder.generateSequenceNumbers;
    }

    /**
     * Instantiate a Builder to be able to create a {@link AggregateRepository} for aggregate type {@code T}.
     * <p>
     * The {@link LockFactory} is defaulted to an {@link NullLockFactory}, thus providing no additional locking, the
     * {@code identifierConverter} to {@link Function#identity()}, the {@link SpanFactory} defaults to a
     * {@link org.axonframework.tracing.NoOpSpanFactory}, and sequence number generation is <b>enabled</b>.
     * <p>
     * A goal of this Builder goal is to create an {@link AggregateModel} specifying generic {@code T} as the aggregate
     * type to be stored. All aggregates in this repository must be {@code instanceOf} this aggregate type. To
     * instantiate this AggregateModel, either an {@link AggregateModel} can be provided directly or an
     * {@code aggregateType} of type {@link Class} can be used. The latter will internally resolve to an AggregateModel.
     * Thus, either the AggregateModel <b>or</b> the {@code aggregateType} should be provided.
     * <p>
     * Additionally, the {@link EntityManagerProvider} and {@link EventBus}  are <b>hard requirements</b> and as such
     * should be provided.
     *
     * @param <T>           The type of aggregate to build the repository for
     * @param aggregateType The type of aggregate to build the repository for
     * @return a Builder to be able to create a {@link AggregateRepository}
     */
    public static <T> Builder<T> builder(Class<T> aggregateType) {
        return new Builder<>(aggregateType);
    }

    @Override
    protected AnnotatedAggregate<T> doLoadWithLock(String aggregateIdentifier, Long expectedVersion) {
        T aggregateRoot = entityManagerProvider.getEntityManager().find(getAggregateType(),
            identifierConverter.apply(aggregateIdentifier),
            LockModeType.PESSIMISTIC_WRITE);
        if (aggregateRoot == null) {
            throw new AggregateNotFoundException(aggregateIdentifier, "%s.notfound".formatted(getAggregateType().getSimpleName().toLowerCase()));
        }
        AnnotatedAggregate<T> aggregate = AnnotatedAggregate.initialize(aggregateRoot,
            aggregateModel(),
            eventBus,
            repositoryProvider);
        if (shouldGenerateSequences()) {
            Optional<Long> sequenceNumber =
                ((DomainEventSequenceAware) eventBus).lastSequenceNumberFor(aggregateIdentifier);
            sequenceNumber.ifPresent(aggregate::initSequence);
        }
        return aggregate;
    }

    @Override
    protected AnnotatedAggregate<T> doCreateNewForLock(Callable<T> factoryMethod) throws Exception {
        // generate sequence numbers in events when using an Event Store
        return AnnotatedAggregate.initialize(factoryMethod,
            aggregateModel(),
            eventBus,
            repositoryProvider,
            shouldGenerateSequences());
    }

    private boolean shouldGenerateSequences() {
        return eventBus instanceof DomainEventSequenceAware && generateSequenceNumbers;
    }

    @Override
    protected void doSaveWithLock(AnnotatedAggregate<T> aggregate) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.persist(aggregate.getAggregateRoot());
        if (forceFlushOnSave) {
            entityManager.flush();
        }
    }

    @Override
    protected void doDeleteWithLock(AnnotatedAggregate<T> aggregate) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.remove(aggregate.getAggregateRoot());
        if (forceFlushOnSave) {
            entityManager.flush();
        }
    }

    /**
     * Indicates whether the EntityManager's state should be flushed each time an aggregate is saved. Defaults to
     * {@code true}.
     * Flushing the EntityManager will force JPA to send state changes to the database. Any key violations and failing
     * optimistic locks will be identified in an early stage.
     *
     * @param forceFlushOnSave whether or not to flush the EntityManager after each save. Defaults to {@code true}.
     * @see jakarta.persistence.EntityManager#flush()
     */
    public void setForceFlushOnSave(boolean forceFlushOnSave) {
        this.forceFlushOnSave = forceFlushOnSave;
    }

    /**
     * Builder class to instantiate a {@link AggregateRepository} for aggregate type {@code T}.
     * <p>
     * The {@link LockFactory} is defaulted to an {@link NullLockFactory}, thus providing no additional locking, the
     * {@code identifierConverter} to {@link Function#identity()}, the {@link SpanFactory} defaults to a
     * {@link org.axonframework.tracing.NoOpSpanFactory}, and sequence number generation is <b>enabled</b>.
     * <p>
     * A goal of this Builder goal is to create an {@link AggregateModel} specifying generic {@code T} as the aggregate
     * type to be stored. All aggregates in this repository must be {@code instanceOf} this aggregate type. To
     * instantiate this AggregateModel, either an {@link AggregateModel} can be provided directly or an
     * {@code aggregateType} of type {@link Class} can be used. The latter will internally resolve to an AggregateModel.
     * Thus, either the AggregateModel <b>or</b> the {@code aggregateType} should be provided.
     * <p>
     * Additionally, the {@link EntityManagerProvider} and {@link EventBus}  are <b>hard requirements</b> and as such
     * should be provided.
     *
     * @param <T> a generic specifying the Aggregate type contained in this {@link org.axonframework.modelling.command.Repository} implementation
     */
    public static class Builder<T> extends LockingRepository.Builder<T> {

        private EntityManagerProvider entityManagerProvider;
        private EventBus eventBus;
        private RepositoryProvider repositoryProvider;
        private Function<String, ?> identifierConverter = Function.identity();
        private boolean generateSequenceNumbers = true;

        /**
         * Creates a builder for a Repository for given {@code aggregateType}.
         *
         * @param aggregateType the {@code aggregateType} specifying the type of aggregate this {@link org.axonframework.modelling.command.Repository} will
         *                      store
         */
        protected Builder(Class<T> aggregateType) {
            super(aggregateType);
            super.lockFactory(NullLockFactory.INSTANCE);
        }

        @Override
        public Builder<T> parameterResolverFactory(@Nonnull ParameterResolverFactory parameterResolverFactory) {
            super.parameterResolverFactory(parameterResolverFactory);
            return this;
        }

        @Override
        public Builder<T> handlerDefinition(@Nonnull HandlerDefinition handlerDefinition) {
            super.handlerDefinition(handlerDefinition);
            return this;
        }

        @Override
        public Builder<T> aggregateModel(@Nonnull AggregateModel<T> aggregateModel) {
            super.aggregateModel(aggregateModel);
            return this;
        }

        @Override
        public Builder<T> lockFactory(LockFactory lockFactory) {
            super.lockFactory(lockFactory);
            return this;
        }

        @Override
        public Builder<T> subtypes(@Nonnull Set<Class<? extends T>> subtypes) {
            super.subtypes(subtypes);
            return this;
        }

        @Override
        public Builder<T> subtype(@Nonnull Class<? extends T> subtype) {
            super.subtype(subtype);
            return this;
        }

        @Override
        public Builder<T> spanFactory(SpanFactory spanFactory) {
            super.spanFactory(spanFactory);
            return this;
        }

        /**
         * Sets the {@link EntityManagerProvider} which provides the {@link EntityManager} instance for this
         * repository.
         *
         * @param entityManagerProvider a {@link EntityManagerProvider} which provides the {@link EntityManager}
         *                              instance for this repository
         * @return the current Builder instance, for fluent interfacing
         */
        public Builder<T> entityManagerProvider(EntityManagerProvider entityManagerProvider) {
            assertNonNull(entityManagerProvider, "EntityManagerProvider may not be null");
            this.entityManagerProvider = entityManagerProvider;
            return this;
        }

        /**
         * Sets the {@link EventBus} to which events are published.
         *
         * @param eventBus an {@link EventBus} to which events are published
         * @return the current Builder instance, for fluent interfacing
         */
        public Builder<T> eventBus(EventBus eventBus) {
            assertNonNull(eventBus, "EventBus may not be null");
            this.eventBus = eventBus;
            return this;
        }

        /**
         * Sets the {@link RepositoryProvider} which services repositories for specific aggregate types.
         *
         * @param repositoryProvider a {@link RepositoryProvider} servicing repositories for specific aggregate types
         * @return the current Builder instance, for fluent interfacing
         */
        public Builder<T> repositoryProvider(RepositoryProvider repositoryProvider) {
            this.repositoryProvider = repositoryProvider;
            return this;
        }

        /**
         * Sets the {@link Function} which converts a {@link String} based identifier to the Identifier object used in
         * the Entity.
         *
         * @param identifierConverter a {@link Function} of input type {@link String} and return type {@code ?} which
         *                            converts the String based identifier to the Identifier object used in the Entity
         * @return the current Builder instance, for fluent interfacing
         */
        public Builder<T> identifierConverter(Function<String, ?> identifierConverter) {
            assertNonNull(identifierConverter, "The identifierConverter may not be null");
            this.identifierConverter = identifierConverter;
            return this;
        }

        /**
         * Disables sequence number generation within this {@link org.axonframework.modelling.command.Repository} implementation.
         * <p>
         * Disabling this feature allows reuse of Aggregate identifiers <b>after</b> removal of the Aggregate instance
         * referred to with said identifier. This opportunity arises from the fact that events published within an
         * Aggregate require a sequence number to change into so-called domain events. These domain events are
         * constrained in the Event Store to have a unique combination of Aggregate identifier to sequence number. Thus,
         * when reusing an Aggregate identifier for which sequences were enabled, will have the Event Store complain
         * with this uniqueness constraint.
         * <p>
         * So disabling sequence number generation will resolve the uniqueness complaints from the Event Store. And, in
         * doing so, allows reuse of an Aggregate identifier.
         * <p>
         * Disabling sequence number generation comes with another cost, though. The events published within a
         * state-stored Aggregate will no longer refer to the Aggregate they originate from.
         *
         * @return The current Builder instance, for fluent interfacing.
         */
        public Builder<T> disableSequenceNumberGeneration() {
            generateSequenceNumbers = false;
            return this;
        }

        /**
         * Initializes a {@link AggregateRepository} as specified through this Builder.
         *
         * @return a {@link AggregateRepository} as specified through this Builder
         */
        public AggregateRepository<T> build() {
            return new AggregateRepository<>(this);
        }

        @Override
        protected void validate() {
            super.validate();
            assertNonNull(entityManagerProvider,
                "The EntityManagerProvider is a hard requirement and should be provided");
            assertNonNull(eventBus, "The EventBus is a hard requirement and should be provided");
        }

    }

}