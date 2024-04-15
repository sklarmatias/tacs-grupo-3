package TACS.TACS.anotaciones;

import TACS.TACS.articulos.Articulo;
import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Anotacion {

    private Usuario usuario;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fecha;
    public Anotacion(){}
    public Anotacion(Usuario usuario){
        this.usuario = usuario;
        this.fecha = new Date();
    }
}
