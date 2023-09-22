package org.dyson.core.aop;

import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;


public interface AuthenticationFacade {
    @Nullable
    Authentication getAuth();

    @Nullable
    String getUserIdentity();
}

