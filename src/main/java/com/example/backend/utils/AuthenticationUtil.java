package com.example.backend.utils;


import com.example.backend.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class AuthenticationUtil {

    protected static final String SIGNER_KEY = "5HIjVwGrrObEkuIDdN2pkmPIU2MqkO0cAXF4pOaUier1KEklSHNTH1aKIuFs48gN";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthenticationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String userId) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(userId).issuer("backend.com").issueTime(new Date()).expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli())).build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return signedJWT.verify(verifier) && expiryTime.after(new Date());
    }

    public String getIdFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    public void createCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", "Beaver_"+token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(3600*24);
        response.addCookie(cookie);
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue().substring("Beaver_".length());
                }
            }
        }
        return null;
    }

    public String authenticateUser(HttpServletRequest request) throws ParseException {
        String token = getTokenFromCookie(request);
        if (token != null) {
            String currentUserId = getIdFromToken(token);
            if (currentUserId != null && userRepository.existsById(currentUserId)) {
                return currentUserId;
            }
            return null;
        }
        return null;

    }

}
