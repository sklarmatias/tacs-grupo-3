package TACS.TACS.articulos;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.usuarios.Usuario;

import java.util.Date;
import java.util.List;

public class Articulo {

    private Integer id;

    private String nombre;

    private String imagen;

    private String link;

    private String usuarioRecibe;

    private EstadoArticulo estado;

    private Date deadline;

    private Usuario propietario;

    private Date fechaDeCreacion;

    private List<Anotacion> anotaciones;

    private Integer cantidadDeAnotaciones;

    private Double costo;

    private TipoCosto tipoCosto;

    private Integer minimoUsuarios;

    private Integer maximoUsuarios;

}
