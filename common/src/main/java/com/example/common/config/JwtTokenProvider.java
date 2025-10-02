package com.example.common.config;

import com.example.common.domain.UserType;
import com.example.common.domain.UserVo;
import com.example.common.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Objects;

public class JwtTokenProvider {

    private String sercretKey = "secretKey";

    private long tokenValidTime = 1000L * 60 * 60 * 24;

    public String createToken(String userPk, Long id, UserType userType) {
        Claims claims =
                Jwts.claims().setSubject(Aes256Util.encrypt(userPk))
                        .setId(Aes256Util.encrypt(id.toString()));

        claims.put("role", userType);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, sercretKey)
                .compact();

    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(sercretKey)
                    .parseClaimsJws(jwtToken);

            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public UserVo getUserVo(String token) {
        Claims c = Jwts.parser()
                .setSigningKey(sercretKey)
                .parseClaimsJws(token)
                .getBody();

        return new UserVo(Long.valueOf(
                Objects.requireNonNull(Aes256Util.decrypt(c.getId()))
        ), Aes256Util.decrypt(c.getSubject()));
    }

    public UserType getRole(String token) {
        Claims c = Jwts.parser()
                .setSigningKey(sercretKey)
                .parseClaimsJws(token)
                .getBody();
        Object role = c.get("role");
        return role instanceof UserType ? (UserType) role : UserType.valueOf(String.valueOf(role));
    }
}


