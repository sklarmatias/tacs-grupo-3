package TACS.TACS.anotaciones;

import TACS.TACS.articulos.Articulo;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;
@Getter
public class Anotacion {

    private Usuario usuario;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fecha;
    public Anotacion(){}
    public Anotacion(int usuario){
        RepositorioDeUsuariosEnMemoria repo = new RepositorioDeUsuariosEnMemoria();
        this.usuario = repo.obtenerUsuario(usuario);
        this.fecha = new Date();
    }
}
