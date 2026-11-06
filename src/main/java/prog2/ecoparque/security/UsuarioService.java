package prog2.ecoparque.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import prog2.ecoparque.model.Permiso;
import prog2.ecoparque.model.Rol;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.repository.UsuarioRepository;

/** Arma la sesion del usuario: sus autoridades son los permisos que tiene su rol. */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        List<GrantedAuthority> autoridades = new ArrayList<>();
        Rol rol = usuario.getRolAsignado();
        if (rol != null) {
            autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
            for (Permiso permiso : rol.getPermisos()) {
                autoridades.add(new SimpleGrantedAuthority(permiso.name()));
            }
        }

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getClave())
                .authorities(autoridades)
                .build();
    }

}
