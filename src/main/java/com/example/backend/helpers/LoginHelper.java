package com.example.backend.helpers;

import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
@Transactional
public class LoginHelper {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String authorizationGrantType;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public String getAccessTokenGoogle(String code, String scope) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        String requestParams =
                "?code=" + code +
                        "&client_id=" + clientId+
                        "&client_secret=" + clientSecret + "&redirect_uri="+ redirectUri+
                        "&scope="+scope +
                        "&grant_type=" + authorizationGrantType;
        Map<String, String> res = restTemplate.postForObject(tokenUrl+requestParams, null, Map.class);
        return res.get("access_token");
    };

    public ResponseEntity loginGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String profileUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        String requestParams = "?access_token=" + accessToken;
        Map<String, String> res = restTemplate.getForObject(profileUrl+requestParams, Map.class);

        if (userService.getUserByEmail(res.get("email")) == null) {
            long currentTime = Instant.now().getEpochSecond();
            String username = String.valueOf(currentTime);

            User user = new User();
            user.setEmail(res.get("email"));
            user.setPassword(res.get("id"));
            user.setName(res.get("name"));
            user.setUsername(username);
            user.setProfilePic(res.get("picture"));

            User result = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(res.get("email")));

    }

}
