package ru.osiptsoff.npaws_auth.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Setter;

@Component
class KeyStore {
    private static final String ACCESS_PUBLIC_KEY = "public_access";
    private static final String ACCESS_PRIVATE_KEY = "private_access";
    private static final String REFRESH_KEY = "refresh";

    private Map<String, Key> keys;

    @Setter
    @Value("${app.config.keyfile}")
    private String keyFilePath;

    public KeyStore() {
        keys = new HashMap<>();
    }

    @PostConstruct
    private void initializeKeys() throws InvalidKeySpecException, NullPointerException, IOException {
        try(var content = Files.lines(Paths.get(keyFilePath))) {
            var stringContent = content
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));
            
            parseKeys(stringContent); 
        } catch (IOException | NoSuchAlgorithmException e) {
            var freshKeys = createAndRememberKeys();
            try(var out = new PrintWriter(keyFilePath)) {
                out.print(freshKeys);
            }
        } 
    }

    public PublicKey getAccessPublicKey() {
        return (PublicKey)keys.get(ACCESS_PUBLIC_KEY);
    }

    public PrivateKey getAccessPrivateKey() {
        return (PrivateKey)keys.get(ACCESS_PRIVATE_KEY);
    }

    public SecretKey getRefreshKey() {
        return (SecretKey)keys.get(REFRESH_KEY);
    }

    private void parseKeys(Map<String, String> stringContent) throws InvalidKeySpecException,
            NoSuchAlgorithmException {
        KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");

        var publicBytes = Base64.getDecoder().decode(stringContent.get(ACCESS_PUBLIC_KEY));
        var pubKeySpec = new X509EncodedKeySpec(publicBytes);
        var pubKey = rsaKeyFactory.generatePublic(pubKeySpec);
        keys.put(ACCESS_PUBLIC_KEY, pubKey);

        publicBytes = Base64.getDecoder().decode(stringContent.get(ACCESS_PRIVATE_KEY));
        var privKeySpec = new PKCS8EncodedKeySpec(publicBytes);
        var privKey = rsaKeyFactory.generatePrivate(privKeySpec);
        keys.put(ACCESS_PRIVATE_KEY, privKey);

        var refreshBytes = Base64.getDecoder().decode(stringContent.get(REFRESH_KEY));
        var refreshKey = Keys.hmacShaKeyFor(refreshBytes);
        keys.put(REFRESH_KEY, refreshKey);
    }

    private StringBuilder createAndRememberKeys() {
        var accessKeyPair = Jwts.SIG.RS256.keyPair().build();
        var refreshKey = Jwts.SIG.HS256.key().build();

        keys.put(ACCESS_PUBLIC_KEY, accessKeyPair.getPublic());
        keys.put(ACCESS_PRIVATE_KEY, accessKeyPair.getPrivate());
        keys.put(REFRESH_KEY, refreshKey);

        var builder = new StringBuilder();
        for(var entry : keys.entrySet()) {
            builder.append(entry.getKey());
            builder.append(":");
            builder.append(Base64.getEncoder().encodeToString(entry.getValue().getEncoded()));
            builder.append("\n");
        }

        return builder;
    }
}