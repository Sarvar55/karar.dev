package org.karar.dev.common.security.service.token.strategy;

import java.util.Map;

/**
 * Implements the Template Method Design Pattern for token handling.
 * This class defines the skeleton of an algorithm for generating, validating, 
 * and extracting tokens, deferring specific steps to subclasses.
 * 
 * By extending this class, you can easily implement both stateless tokens (like JWT)
 * and stateful/opaque tokens (like UUIDs stored in Redis), while maintaining a 
 * consistent flow and behavior across the application.
 */
public abstract class AbstractTokenStrategy implements TokenStrategy {

    // ========================================================================
    // 1. TEMPLATE METHODS (The Skeleton - usually final so subclasses can't change the flow)
    // ========================================================================

    @Override
    public final String generate(String username) {
        return generate(username, Map.of());
    }

    @Override
    public final String generate(String username, Map<String, Object> claims) {
        // Step 1: Hook for pre-processing (e.g., adding default claims)
        Map<String, Object> enrichedClaims = enrichClaims(username, claims);
        
        // Step 2: Abstract step to actually create the token (JWT creation or UUID generation)
        String token = doGenerate(username, enrichedClaims);
        
        // Step 3: Hook for post-processing (e.g., storing opaque token in Redis/DB)
        postGenerate(username, token, enrichedClaims);
        
        return token;
    }

    @Override
    public final boolean validate(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // Step 1: Hook for quick format validation (e.g., check if it's a valid UUID or JWT format)
        if (!isFormatValid(token)) {
            return false;
        }
        
        // Step 2: Abstract step for actual validation (JWT signature check or Redis lookup)
        return doValidate(token);
    }

    @Override
    public final String extractUsername(String token) {
        if (!validate(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return doExtractUsername(token);
    }

    // ========================================================================
    // 2. ABSTRACT METHODS (Must be implemented by subclasses like JWT or Opaque)
    // ========================================================================

    /**
     * Performs the actual token generation (e.g., building a JWT or a random UUID).
     */
    protected abstract String doGenerate(String username, Map<String, Object> claims);
    
    /**
     * Performs the actual token validation (e.g., verifying JWT signature or checking Redis).
     */
    protected abstract boolean doValidate(String token);
    
    /**
     * Extracts the username from the token. 
     * For JWT, it parses the claims. For Opaque, it queries Redis/DB.
     */
    protected abstract String doExtractUsername(String token);

    // ========================================================================
    // 3. HOOK METHODS (Optional extensions for subclasses)
    // ========================================================================

    protected Map<String, Object> enrichClaims(String username, Map<String, Object> claims) {
        return claims; // Default: no enrichment
    }

    protected void postGenerate(String username, String token, Map<String, Object> claims) {
        // Default: do nothing. Opaque tokens can override this to save the token to Redis.
    }

    protected boolean isFormatValid(String token) {
        return true; // Default: always valid format
    }
}
