package org.dyson.core.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.dyson.core.security.AuthenticationFacade;
import org.dyson.core.security.impl.AuthenticationFacadeImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFacade authenticationFacade() {
        return new AuthenticationFacadeImpl();
    }

}
