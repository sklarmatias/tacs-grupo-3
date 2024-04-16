package TACS.TACS.repositorios.usuarios;

import TACS.TACS.usuarios.Usuario;

import java.util.ArrayList;
import java.util.List;
public class RepositorioDeUsuariosEnMemoria implements RepositorioDeUsuarios{

    private static final List<Usuario> usuarios = new ArrayList<>();
    private static Integer clave = 0;


    @Override
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Usuario obtenerUsuario(Integer id) {
        return usuarios.stream().filter(us -> us.getId().equals(id)).findFirst().get();
    }

    @Override
    public void actualizarUsuario(Integer id, Usuario usuario) {
        Usuario useroriginal = usuarios.stream().filter(us -> us.getId().equals(id)).findFirst().get();
        useroriginal.actualizarValores(usuario);
    }

    @Override
    public Integer guardarUsuario(Usuario usuario) {
        clave += 1;
        usuario.setId(clave);
        usuarios.add(usuario);
        return clave;
    }

    @Override
    public void borrarUsuarios(){
        usuarios.clear();
        clave = 0;
    }
}
