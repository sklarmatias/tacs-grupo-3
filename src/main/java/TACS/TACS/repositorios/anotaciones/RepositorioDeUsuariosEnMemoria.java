package TACS.TACS.repositorios.anotaciones;

import TACS.TACS.usuarios.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioDeUsuariosEnMemoria implements RepositorioDeUsuarios{

    private Map<Integer, Usuario> usuarios;
    private Integer clave;

    public RepositorioDeUsuariosEnMemoria(){
        usuarios = new HashMap<>();
        clave = 0;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public Usuario obtenerUsuario(Integer id) {
        return usuarios.get(id);
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    @Override
    public Integer guardarUsuario(Usuario usuario) {
        clave += 1;
        usuarios.put(clave, usuario);
        return clave;
    }
}
