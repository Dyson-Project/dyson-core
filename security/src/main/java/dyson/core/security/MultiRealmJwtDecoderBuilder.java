package dyson.core.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.cache.Cache;
import org.springframework.http.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 */
public class MultiRealmJwtDecoderBuilder {

    private JWKSetRepository issuerUriRepository;

    private Set<SignatureAlgorithm> signatureAlgorithms = new HashSet<>();

    private RestOperations restOperations = new RestTemplate();

    private Cache cache;

    private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;

    public MultiRealmJwtDecoderBuilder(JWKSetRepository issuerUriRepository) {
        Assert.notNull(issuerUriRepository, "issuerUriRepository cannot be null");
        this.issuerUriRepository = issuerUriRepository;
        this.jwtProcessorCustomizer = (processor) -> {
        };
    }

    /**
     * @param url
     * @return
     */
    private static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid JWK Set URL \"" + url + "\" : " + ex.getMessage(), ex);
        }
    }

    /**
     * @param signatureAlgorithm
     * @return
     */
    public MultiRealmJwtDecoderBuilder jwsAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        this.signatureAlgorithms.add(signatureAlgorithm);
        return this;
    }

    /**
     * @param signatureAlgorithmsConsumer
     * @return
     */
    public MultiRealmJwtDecoderBuilder jwsAlgorithms(Consumer<Set<SignatureAlgorithm>> signatureAlgorithmsConsumer) {
        Assert.notNull(signatureAlgorithmsConsumer, "signatureAlgorithmsConsumer cannot be null");
        signatureAlgorithmsConsumer.accept(this.signatureAlgorithms);
        return this;
    }

    /**
     * @param restOperations
     * @return
     */
    public MultiRealmJwtDecoderBuilder restOperations(RestOperations restOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.restOperations = restOperations;
        return this;
    }

    /**
     * Use the given {@link Cache} to store
     * <a href="https://tools.ietf.org/html/rfc7517#section-5">JWK Set</a>.
     *
     * @param cache the {@link Cache} to be used to store JWK Set
     * @return a {@link NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder} for further configurations
     * @since 5.4
     */
    public MultiRealmJwtDecoderBuilder cache(Cache cache) {
        Assert.notNull(cache, "cache cannot be null");
        this.cache = cache;
        return this;
    }

    public MultiRealmJwtDecoderBuilder jwtProcessorCustomizer(
        Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
        Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
        this.jwtProcessorCustomizer = jwtProcessorCustomizer;
        return this;
    }

    JWTClaimsSetAwareJWSKeySelector<SecurityContext> jwsKeySelector() {
        if (this.signatureAlgorithms.isEmpty()) {
            return new MultiRealmJwtClaimsSetAwareJWSKeySelector(JWSAlgorithm.RS256, issuerUriRepository);
        }
        Set<JWSAlgorithm> jwsAlgorithms = new HashSet<>();
        for (SignatureAlgorithm signatureAlgorithm : this.signatureAlgorithms) {
            JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(signatureAlgorithm.getName());
            jwsAlgorithms.add(jwsAlgorithm);
        }
        return new MultiRealmJwtClaimsSetAwareJWSKeySelector(jwsAlgorithms, issuerUriRepository);
    }

    JWTProcessor<SecurityContext> processor() {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(jwsKeySelector());
        // Spring Security validates the claim set independent from Nimbus
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
        this.jwtProcessorCustomizer.accept(jwtProcessor);
        return jwtProcessor;
    }

    /**
     * Build the configured {@link NimbusJwtDecoder}.
     *
     * @return the configured {@link NimbusJwtDecoder}
     */
    public NimbusJwtDecoder build() {
        return new NimbusJwtDecoder(processor());
    }

    private static final class SpringJWKSetCache implements JWKSetCache {

        private final String jwkSetUri;

        private final Cache cache;

        private JWKSet jwkSet;

        SpringJWKSetCache(String jwkSetUri, Cache cache) {
            this.jwkSetUri = jwkSetUri;
            this.cache = cache;
            this.updateJwkSetFromCache();
        }

        private void updateJwkSetFromCache() {
            String cachedJwkSet = this.cache.get(this.jwkSetUri, String.class);
            if (cachedJwkSet != null) {
                try {
                    this.jwkSet = JWKSet.parse(cachedJwkSet);
                } catch (ParseException ignored) {
                    // Ignore invalid cache value
                }
            }
        }

        // Note: Only called from inside a synchronized block in RemoteJWKSet.
        @Override
        public void put(JWKSet jwkSet) {
            this.jwkSet = jwkSet;
            this.cache.put(this.jwkSetUri, jwkSet.toString(false));
        }

        @Override
        public JWKSet get() {
            return (!requiresRefresh()) ? this.jwkSet : null;

        }

        @Override
        public boolean requiresRefresh() {
            return this.cache.get(this.jwkSetUri) == null;
        }

    }

    private static class RestOperationsResourceRetriever implements ResourceRetriever {

        private static final MediaType APPLICATION_JWK_SET_JSON = new MediaType("application", "jwk-set+json");

        private final RestOperations restOperations;

        RestOperationsResourceRetriever(RestOperations restOperations) {
            Assert.notNull(restOperations, "restOperations cannot be null");
            this.restOperations = restOperations;
        }

        @Override
        public Resource retrieveResource(URL url) throws IOException {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, APPLICATION_JWK_SET_JSON));
            ResponseEntity<String> response = getResponse(url, headers);
            if (response.getStatusCodeValue() != 200) {
                throw new IOException(response.toString());
            }
            return new Resource(response.getBody(), "UTF-8");
        }

        private ResponseEntity<String> getResponse(URL url, HttpHeaders headers) throws IOException {
            try {
                RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, url.toURI());
                return this.restOperations.exchange(request, String.class);
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }

    }
}
