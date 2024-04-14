package TACS.TACS.repositorios.anotaciones;

import TACS.TACS.usuarios.Usuario;

import java.util.List;

public interface RepositorioDeUsuarios {

    List<Usuario> listarUsuarios();

    Usuario obtenerUsuario(Integer id);

    void actualizarUsuario(Usuario usuario);

    Integer guardarUsuario(Usuario usuario);



}
