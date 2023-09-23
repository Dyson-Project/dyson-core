package org.dyson.core.state

class CannotChangeState(
    val currentState: State,
    val nextState: State
) : IllegalStateException("cannot.change.state")