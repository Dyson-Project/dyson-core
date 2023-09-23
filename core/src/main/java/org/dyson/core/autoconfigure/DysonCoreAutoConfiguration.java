package org.dyson.core.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.dyson.core.aop.AuthenticationFacade;
import org.dyson.core.aop.impl.AuthenticationFacadeImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class DysonCoreAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFacade authenticationFacade() {
        return new AuthenticationFacadeImpl();
    }


}
