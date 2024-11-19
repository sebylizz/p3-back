package dk.leghetto.services;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

import dk.leghetto.classes.CustomerRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TokenService {
    @Inject
    CustomerRepository cr;

    public String generateToken(String email) {
        String token = Jwt.issuer("https://leghetto.dk/issuer")
                .upn(email)
                .groups(getRoles(cr.findByEmail(email).getRole()))
                .expiresIn(Duration.ofHours(8))
                .sign();
        return token;
    }

    public HashSet<String> getRoles(String role) {
        if (role.equals("admin")) {
            return new HashSet<>(Arrays.asList("user", "admin"));
        } else {
            return new HashSet<>(Arrays.asList("user"));
        }
    }

}
