package TACS.TACS.usuarios;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import lombok.Getter;

import java.util.List;

@Getter
public class Usuario {

    private Integer id;

    private String nombre;

    private String apellido;

    private String mail;

    private List<Articulo> articulosPublicados;

    private List<Anotacion> anotaciones;

}
