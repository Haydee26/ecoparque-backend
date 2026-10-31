package prog2.ecoparque.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/usuarios/yo").authenticated()
                // Datos propios y lista de guias sin datos personales: cualquier usuario con sesion.
                .requestMatchers(HttpMethod.GET, "/api/personal/yo").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/guias/publico").authenticated()
                .requestMatchers("/api/usuarios", "/api/usuarios/**").hasRole("ADMINISTRADOR")
                // Registro numerico del parque: solo el administrador lo ve.
                .requestMatchers("/api/parque", "/api/parque/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/resumen", "/api/resumen/**").hasRole("ADMINISTRADOR")
                // Datos personales completos de guias y cuidadores: solo el administrador.
                .requestMatchers("/api/guias", "/api/guias/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/cuidadores", "/api/cuidadores/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers("/api/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
                )
                .httpBasic(basic -> {
                });

        return http.build();
    }
}
