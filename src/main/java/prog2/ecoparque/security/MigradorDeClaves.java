package prog2.ecoparque.security;

import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.repository.UsuarioRepository;

/**
 * Al arrancar, encripta las claves que todavía están guardadas como texto común
 * (las de antes de usar BCrypt). Si ya están encriptadas no toca nada, así que
 * se puede ejecutar todas las veces que haga falta.
 */
@Component
public class MigradorDeClaves implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificador;

    public MigradorDeClaves(UsuarioRepository usuarioRepository, PasswordEncoder codificador) {
        this.usuarioRepository = usuarioRepository;
        this.codificador = codificador;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (!estaEncriptada(usuario.getClave())) {
                usuario.setClave(codificador.encode(usuario.getClave()));
                usuarioRepository.save(usuario);
            }
        }
    }

    /** Las claves BCrypt empiezan con $2a$, $2b$ o $2y$. */
    static boolean estaEncriptada(String clave) {
        return clave != null && clave.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
