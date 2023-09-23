package dyson.core.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MultiRealmJwtClaimsSetAwareJWSKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {
    private final Set<JWSAlgorithm> jwsAlgs;
    private final JWKSetRepository issuerUriRepository;

    public MultiRealmJwtClaimsSetAwareJWSKeySelector(final Set<JWSAlgorithm> jwsAlgs, final JWKSetRepository issuerUriRepository) {
        if (jwsAlgs == null || jwsAlgs.isEmpty()) {
            throw new IllegalArgumentException("The JWS algorithms must not be null or empty");
        }
        this.jwsAlgs = Collections.unmodifiableSet(jwsAlgs);
        this.issuerUriRepository = issuerUriRepository;
    }

    public MultiRealmJwtClaimsSetAwareJWSKeySelector(final JWSAlgorithm jwsAlg, final JWKSetRepository issuerUriRepository) {
        this(Collections.singleton(jwsAlg), issuerUriRepository);
    }

    public boolean isAllowed(final JWSAlgorithm jwsAlg) {
        return jwsAlgs.contains(jwsAlg);
    }

    protected JWKMatcher createJWKMatcher(final JWSHeader jwsHeader) {

        if (!isAllowed(jwsHeader.getAlgorithm())) {
            // Unexpected JWS alg
            return null;
        } else {
            return JWKMatcher.forJWSHeader(jwsHeader);
        }
    }

    @Override
    public List<? extends Key> selectKeys(JWSHeader header, JWTClaimsSet claimsSet, SecurityContext context) throws KeySourceException {
        if (!jwsAlgs.contains(header.getAlgorithm())) {
            // Unexpected JWS alg
            return Collections.emptyList();
        }

        JWKMatcher jwkMatcher = createJWKMatcher(header);
        if (jwkMatcher == null) {
            return Collections.emptyList();
        }
        JWKSet jwkSet = null;
        try {
            jwkSet = issuerUriRepository.getJWKSetByIssuerClaim(claimsSet.getIssuer());
        } catch (IOException | ParseException e) {
            throw new KeySourceException(e.getMessage(), e);
        }
        List<JWK> jwkMatches = new JWKSelector(jwkMatcher).select(jwkSet);

        List<Key> sanitizedKeyList = new LinkedList<>();

        for (Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (key instanceof PublicKey || key instanceof SecretKey) {
                sanitizedKeyList.add(key);
            } // skip asymmetric private keys
        }

        return sanitizedKeyList;
    }
}
