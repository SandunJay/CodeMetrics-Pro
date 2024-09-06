//package com.sliit.spm.codecomplexityanalyzer.service.auth;
//
//import io.jsonwebtoken.*;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//    private final String jwtSecret = "yourSecretKey";
//    private final long jwtExpirationMs = 3600000;  // 1 hour
//
//    public String generateToken(String email) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
//
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }
//
//    public String getEmailFromToken(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
//            // Handle invalid JWT exceptions
//        }
//        return false;
//    }
//}
