package TACS.TACS.repositorios.usuarios;

import TACS.TACS.usuarios.Usuario;

import java.util.List;

public interface RepositorioDeUsuarios {

    List<Usuario> listarUsuarios();

    Usuario obtenerUsuario(Integer id);

    void actualizarUsuario(Integer id,Usuario usuario);

    Integer guardarUsuario(Usuario usuario);



}
