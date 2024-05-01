package TACS.TACS.usuarios.repositorios;

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
        if (usuario.getNombre() != null) {
            useroriginal.setNombre(usuario.getNombre());
        }
        if (usuario.getApellido() != null) {
            useroriginal.setApellido(usuario.getApellido());
        }
        if (usuario.getMail() != null) {
            useroriginal.setMail(usuario.getMail());
        }
    }

    @Override
    public Integer guardarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null)
            throw new IllegalArgumentException("El valor del campo \"nombre\" es obligatorio");
        if (usuario.getApellido() == null)
            throw new IllegalArgumentException("El valor del campo \"apellido\" es obligatorio");
        if (usuario.getMail() == null)
            throw new IllegalArgumentException("El valor del campo \"email\" es obligatorio");
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
