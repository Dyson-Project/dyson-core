package dyson.core.security;

import com.nimbusds.jose.jwk.JWKSet;

import java.io.IOException;
import java.text.ParseException;

public interface JWKSetRepository {
    /**
     * JWTSet la object co the cache <br>
     * TODO: cache me
     */
    JWKSet getJWKSetByIssuerClaim(String iss) throws IOException, ParseException;
}
