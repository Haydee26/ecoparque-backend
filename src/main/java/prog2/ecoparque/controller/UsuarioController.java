package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.Usuario;
import java.security.Principal;
import prog2.ecoparque.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
    }
    
    @GetMapping("/yo")
    public Usuario yo(Principal principal) {
        return usuarioRepository.findByNombreUsuario(principal.getName()).orElseThrow();
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        usuario.setRol(usuarioActualizado.getRol());
        usuario.setNombrePersona(usuarioActualizado.getNombrePersona());
        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().isBlank()) {
            usuario.setClave(usuarioActualizado.getClave());
        }
        return usuarioRepository.save(usuario);
    }
}
