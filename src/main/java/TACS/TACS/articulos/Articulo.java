package TACS.TACS.articulos;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class Articulo {

    private Integer id;

    private String nombre;

    private String imagen;

    private String link;

    private String usuarioRecibe;

    private EstadoArticulo estado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    private Usuario propietario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaDeCreacion;

    private List<Anotacion> anotaciones;

    private Integer cantidadDeAnotaciones;

    private Double costo;

    private TipoCosto tipoCosto;

    private Integer minimoUsuarios;

    private Integer maximoUsuarios;

    public void agregarAnotacion(Anotacion anotacion) {
        if(anotaciones == null){
            anotaciones = new ArrayList<>();
        }
    	anotaciones.add(anotacion);
    	cantidadDeAnotaciones++;
    }
    public Articulo(){}
    public Articulo(Integer id, String nombre, String imagen, String link, String usuarioRecibe, EstadoArticulo estado,
                    Date deadline, Integer propietario, Double costo, TipoCosto tipoCosto, Integer minimoUsuarios,
                    Integer maximoUsuarios) {
        RepositorioDeUsuariosEnMemoria repo = new RepositorioDeUsuariosEnMemoria();
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.link = link;
        this.usuarioRecibe = usuarioRecibe;
        this.estado = estado;
        this.deadline = deadline;
        this.propietario = repo.obtenerUsuario(propietario);
        this.fechaDeCreacion = new Date();
        this.anotaciones = new ArrayList<>();
        this.cantidadDeAnotaciones = 0;
        this.costo = costo;
        this.tipoCosto = tipoCosto;
        this.minimoUsuarios = minimoUsuarios;
        this.maximoUsuarios = maximoUsuarios;
    }
}
