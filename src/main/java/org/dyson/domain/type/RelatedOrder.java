package org.dyson.domain.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dyson.core.model.ValueObject;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class RelatedOrder implements ValueObject {
    private final UUID orderId;
    private final Price total;
}
