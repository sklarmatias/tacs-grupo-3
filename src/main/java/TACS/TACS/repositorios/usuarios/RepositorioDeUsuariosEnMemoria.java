package TACS.TACS.repositorios.usuarios;

import TACS.TACS.usuarios.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioDeUsuariosEnMemoria implements RepositorioDeUsuarios{

    private static Map<Integer, Usuario> usuarios = new HashMap<>();
    private static Integer clave = 0;


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
        usuarios.put(usuario.getId(), usuario);
        return clave;
    }
    public void vaciarRepositorio(){
        usuarios = new HashMap<>();
        clave = 0;
    }
}
