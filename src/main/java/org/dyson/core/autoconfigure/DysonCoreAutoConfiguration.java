package org.dyson.core.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.dyson.core.aop.AuthenticationFacade;
import org.dyson.core.aop.impl.AuthenticationFacadeImpl;
import org.dyson.core.aop.impl.MeasureTimeAdvice;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
@AutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DysonCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MeasureTimeAdvice measureTimeAdvice() {
        return new MeasureTimeAdvice();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFacade authenticationFacade() {
        return new AuthenticationFacadeImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {
            @NotNull
            @Override
            public Optional<String> getCurrentAuditor() {
                var auth = SecurityContextHolder.getContext()
                    .getAuthentication();
                var username = auth != null ? auth.getName() : null;
                return Optional.ofNullable(username);
            }
        };
    }

}
