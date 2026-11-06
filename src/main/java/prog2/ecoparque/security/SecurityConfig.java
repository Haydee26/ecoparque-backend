package prog2.ecoparque.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad por permisos: cada seccion pide su permiso VER para consultar y
 * EDITAR para dar de alta, baja o modificar. Los permisos los otorga cada rol
 * (lo arma el administrador).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Permisos de consulta del "catalogo del parque": sus datos se usan unos con otros
    // (una especie necesita ver hábitats, una zona muestra itinerarios...), asi que para
    // LEER cualquiera de ellos alcanza con tener alguno de estos permisos.
    private static final String[] VER_CATALOGO = {"ZONAS_VER", "HABITATS_VER", "ESPECIES_VER",
        "ITINERARIOS_VER", "ASIGNACIONES_VER", "RESPONSABILIDADES_VER"};

    /**
     * Las claves se guardan encriptadas con BCrypt: ni en la base se pueden
     * leer.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                // Datos propios, permisos propios y lista de guias sin datos personales: cualquier usuario con sesion.
                .requestMatchers(HttpMethod.GET, "/api/usuarios/yo").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/personal/yo").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/guias/publico").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/roles/mis-permisos").authenticated()
                // Roles (la lista tambien la necesita la pantalla de usuarios para elegir el rol).
                .requestMatchers(HttpMethod.GET, "/api/roles", "/api/roles/**")
                .hasAnyAuthority("ROLES_VER", "USUARIOS_VER", "USUARIOS_EDITAR")
                .requestMatchers("/api/roles", "/api/roles/**").hasAuthority("ROLES_EDITAR")
                // Datos personales y administracion.
                .requestMatchers(HttpMethod.GET, "/api/empleados", "/api/empleados/**").hasAuthority("EMPLEADOS_VER")
                .requestMatchers("/api/empleados", "/api/empleados/**").hasAuthority("EMPLEADOS_EDITAR")
                .requestMatchers(HttpMethod.GET, "/api/usuarios", "/api/usuarios/**").hasAuthority("USUARIOS_VER")
                .requestMatchers("/api/usuarios", "/api/usuarios/**").hasAuthority("USUARIOS_EDITAR")
                .requestMatchers(HttpMethod.GET, "/api/guias", "/api/guias/**").hasAuthority("GUIAS_VER")
                .requestMatchers("/api/guias", "/api/guias/**").hasAuthority("GUIAS_EDITAR")
                .requestMatchers(HttpMethod.GET, "/api/cuidadores", "/api/cuidadores/**").hasAuthority("CUIDADORES_VER")
                .requestMatchers("/api/cuidadores", "/api/cuidadores/**").hasAuthority("CUIDADORES_EDITAR")
                // Registro numerico del parque.
                .requestMatchers(HttpMethod.GET, "/api/parque", "/api/parque/**", "/api/resumen", "/api/resumen/**")
                .hasAuthority("PARQUE_VER")
                .requestMatchers("/api/parque", "/api/parque/**", "/api/resumen", "/api/resumen/**")
                .hasAuthority("PARQUE_EDITAR")
                // Salud de las especies (tiene que ir ANTES del catalogo, porque se usa la primera regla que coincide).
                .requestMatchers(HttpMethod.GET, "/api/especies/enfermas").hasAuthority("SALUD_VER")
                .requestMatchers(HttpMethod.PUT, "/api/especies/*/enferma", "/api/especies/*/sana")
                .hasAuthority("SALUD_EDITAR")
                // Catalogo del parque: leer con cualquiera de los permisos VER; escribir con el de cada seccion.
                .requestMatchers(HttpMethod.GET, "/api/zonas", "/api/zonas/**", "/api/habitats", "/api/habitats/**",
                        "/api/especies", "/api/especies/**", "/api/itinerarios", "/api/itinerarios/**",
                        "/api/asignaciones-guia", "/api/asignaciones-guia/**",
                        "/api/responsabilidades-cuidador", "/api/responsabilidades-cuidador/**")
                .hasAnyAuthority(VER_CATALOGO)
                .requestMatchers("/api/zonas", "/api/zonas/**").hasAuthority("ZONAS_EDITAR")
                .requestMatchers("/api/habitats", "/api/habitats/**").hasAuthority("HABITATS_EDITAR")
                .requestMatchers("/api/especies", "/api/especies/**").hasAuthority("ESPECIES_EDITAR")
                .requestMatchers("/api/itinerarios", "/api/itinerarios/**").hasAuthority("ITINERARIOS_EDITAR")
                .requestMatchers("/api/asignaciones-guia", "/api/asignaciones-guia/**").hasAuthority("ASIGNACIONES_EDITAR")
                .requestMatchers("/api/responsabilidades-cuidador", "/api/responsabilidades-cuidador/**")
                .hasAuthority("RESPONSABILIDADES_EDITAR")
                // Cualquier otra cosa de la API: nadie (por si se agrega una ruta y se olvida el permiso).
                .requestMatchers("/api/**").denyAll()
                .anyRequest().authenticated()
                )
                .httpBasic(basic -> {
                });

        return http.build();
    }
}
