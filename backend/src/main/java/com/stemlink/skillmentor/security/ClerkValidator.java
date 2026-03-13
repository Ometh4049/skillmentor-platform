package com.stemlink.skillmentor.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class ClerkValidator implements TokenValidator {

    private final JwkProvider jwkProvider;

    public ClerkValidator(@Value("${clerk.jwks.url}") String clerkJwksUrl) {
        try {
            this.jwkProvider = new UrlJwkProvider(new URL(clerkJwksUrl));
        } catch (Exception e) {
            log.error("Failed to initialize JwkProvider with URL: {}", clerkJwksUrl, e);
            throw new RuntimeException("Failed to initialize Clerk validator", e);
        }
    }

    @Override
    public boolean validateToken(String token){
        try {
            // Step 1: Decode JWT without verification to get header info
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT == null) {
                log.error("Failed to decode token");
                return false;
            }

            // Step 2: Extract key ID (kid) from the token header
            String kid = decodedJWT.getKeyId();
            if (kid == null || kid.isEmpty()) {
                log.error("Token does not contain a key ID (kid)");
                return false;
            }

            log.debug("Token kid: {}", kid);

            // Step 3: Fetch JWK and verify signature
            if (!verifyTokenSignature(token, kid)) {
                log.error("Token signature verification failed");
                return false;
            }

            log.debug("Token validation successful for subject: {}", decodedJWT.getSubject());
            return true;

        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String extractUserId(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            return decodedJWT != null ? decodedJWT.getSubject() : null;
        } catch (Exception e) {
            log.error("Error extracting user ID: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> extractRoles(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT == null) {
                return null;
            }

            List<String> roles = new ArrayList<>();

            List<String> directRoles = decodedJWT.getClaim("roles").asList(String.class);
            if (directRoles != null) {
                roles.addAll(directRoles);
            }

            String directRole = decodedJWT.getClaim("role").asString();
            if (directRole != null && !directRole.isBlank()) {
                roles.add(directRole);
            }

            Map<String, Object> publicMetadata = decodedJWT.getClaim("public_metadata").asMap();
            if (publicMetadata == null) {
                publicMetadata = decodedJWT.getClaim("publicMetadata").asMap();
            }

            if (publicMetadata != null) {
                Object metadataRoles = publicMetadata.get("roles");
                if (metadataRoles instanceof List<?> list) {
                    for (Object role : list) {
                        if (role != null) {
                            roles.add(String.valueOf(role));
                        }
                    }
                }
                Object metadataRole = publicMetadata.get("role");
                if (metadataRole != null) {
                    roles.add(String.valueOf(metadataRole));
                }
            }

            List<String> normalized = roles.stream()
                    .filter(role -> role != null && !role.isBlank())
                    .map(role -> role.trim().toUpperCase(Locale.ROOT))
                    .distinct()
                    .toList();

            if (normalized.isEmpty()) {
                return List.of("STUDENT");
            }

            return normalized;
        } catch (Exception e) {
            log.error("Error extracting roles: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractFirstName(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT == null) {
                return null;
            }

            String value = firstNonBlank(
                    decodedJWT.getClaim("first_name").asString(),
                    decodedJWT.getClaim("given_name").asString()
            );

            if (value != null) {
                return value;
            }

            return readStringFromMapClaim(decodedJWT, "public_metadata", "firstName");
        } catch (Exception e) {
            log.error("Error extracting first name: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractLastName(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT == null) {
                return null;
            }

            String value = firstNonBlank(
                    decodedJWT.getClaim("last_name").asString(),
                    decodedJWT.getClaim("family_name").asString()
            );

            if (value != null) {
                return value;
            }

            return readStringFromMapClaim(decodedJWT, "public_metadata", "lastName");
        } catch (Exception e) {
            log.error("Error extracting last name: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractEmail(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT == null) {
                return null;
            }

            String directEmail = firstNonBlank(
                    decodedJWT.getClaim("email").asString(),
                    decodedJWT.getClaim("email_address").asString()
            );
            if (directEmail != null) {
                return directEmail;
            }

            String metadataEmail = readStringFromMapClaim(decodedJWT, "public_metadata", "email");
            if (metadataEmail != null) {
                return metadataEmail;
            }

            Map<String, Object> primaryEmailAddress = decodedJWT.getClaim("primary_email_address").asMap();
            if (primaryEmailAddress != null) {
                Object emailAddress = primaryEmailAddress.get("email_address");
                if (emailAddress instanceof String value && !value.isBlank()) {
                    return value;
                }
            }

            List<?> emailAddresses = decodedJWT.getClaim("email_addresses").asList(Map.class);
            if (emailAddresses != null) {
                for (Object item : emailAddresses) {
                    if (item instanceof Map<?, ?> emailAddress) {
                        Object value = emailAddress.get("email_address");
                        if (value instanceof String email && !email.isBlank()) {
                            return email;
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error extracting email: {}", e.getMessage());
            return null;
        }
    }

    private String readStringFromMapClaim(DecodedJWT decodedJWT, String claimName, String key) {
        Map<String, Object> claim = decodedJWT.getClaim(claimName).asMap();
        if (claim == null) {
            claim = decodedJWT.getClaim(toCamelCase(claimName)).asMap();
        }
        if (claim == null) {
            return null;
        }
        Object value = claim.get(key);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        return null;
    }

    private String toCamelCase(String value) {
        if (Objects.equals(value, "public_metadata")) {
            return "publicMetadata";
        }
        return value;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }


    private DecodedJWT decodeToken(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception e) {
            log.error("Failed to decode token: {}", e.getMessage());
            return null;
        }
    }

    private boolean verifyTokenSignature(String token, String kid) {
        try {
            // Fetch the JWK from Clerk
            Jwk jwk = jwkProvider.get(kid);

            // Get the public key from the JWK
            PublicKey publicKey = jwk.getPublicKey();

            // Create algorithm and verify the token
            Algorithm algorithm = Algorithm.RSA256((java.security.interfaces.RSAPublicKey) publicKey, null);
            JWT.require(algorithm).build().verify(token);

            log.debug("Token signature verified successfully for kid: {}", kid);
            return true;

        } catch (Exception e) {
            log.error("Signature verification failed for kid {}: {}", kid, e.getMessage());
            return false;
        }
    }

}
