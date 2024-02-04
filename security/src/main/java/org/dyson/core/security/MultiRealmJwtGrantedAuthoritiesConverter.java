package org.dyson.core.security;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 */
public class MultiRealmJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Log logger = LogFactory.getLog(getClass());
    private String authorityPrefix = "";

    private String authoritiesClaimName = "resource_access";

    /**
     * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
     *
     * @param jwt The {@link Jwt} token
     * @return The {@link GrantedAuthority authorities} read from the token scopes
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
        }
        return grantedAuthorities;
    }

    /**
     * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by this
     * converter. Defaults to
     *
     * @param authorityPrefix The authority prefix
     * @since 5.2
     */
    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    /**
     * Sets the name of token claim to use for mapping {@link GrantedAuthority
     * authorities} by this converter. Defaults to
     *
     * @param authoritiesClaimName The token claim name to map authorities
     * @since 5.2
     */
    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {
        return this.authoritiesClaimName;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            this.logger.trace("Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Looking for scopes in claim %s", claimName));
        }
        LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>> authorities = jwt.getClaim(claimName);
        return Stream.of(
            getAccountAuthorities(authorities),
            getCurrenBranchAuthorities(authorities),
            getCurrentRealmAuthorities(authorities)
        ).parallel().flatMap(Collection::parallelStream).toList();
    }

    private Collection<String> getCurrenBranchAuthorities(LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>> authorities) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String[] parts = request.getRequestURI().split("/");
        String currentBranchId = null;
        for (int i = 0; i < parts.length; i++) {
            if (Objects.equals(parts[i], "branches") && i < parts.length - 1) {
                currentBranchId = parts[i + 1];
                break;
            }
        }
        if (currentBranchId == null || !authorities.containsKey(currentBranchId)) {
            return Collections.emptyList();
        }
        return authorities.get(currentBranchId).get("roles");
    }

    private Collection<String> getAccountAuthorities(LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>> authorities) {
        return authorities.get("account").get("roles");
    }

    private Collection<String> getCurrentRealmAuthorities(LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>> authorities) {
        // TODO: complete me
        return Collections.emptyList();
    }
}
