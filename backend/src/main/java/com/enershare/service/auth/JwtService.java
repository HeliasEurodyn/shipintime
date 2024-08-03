package com.enershare.service.auth;

import com.enershare.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);

    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("userRole", user.getRole());
        return buildToken(claims, user, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("userRole", user.getRole());
        return generateToken(claims, user);
    }

    public boolean isTokenValid(String token, User user) {
        final String userId = extractClaim("userId");
        return (userId.equals(user.getId()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserIdByToken(String token) {
        if (token == null) {
            return "";
        }
        Claims claims = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String getUserId() {

        String token = this.getJwt();

        if(token == null){
            return "";
        }

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return  claims.get("userId", String.class);
    }

    public String extractClaim(String key) {

        String token = this.getJwt();

        if(token == null){
            return "";
        }

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return  claims.get(key, String.class);
    }

    private String getJwt() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        if(servletRequestAttributes == null) {
            return null;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }


}
