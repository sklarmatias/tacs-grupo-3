package TACS.TACS.articulos;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Articulo {

    private Integer id;

    private String nombre;

    private String imagen;

    private String link;

    private String usuarioRecibe;

    private EstadoArticulo estado;

    // TODO sacar de la entidad, hacerlo en el servicio
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    private Usuario propietario;

    // TODO sacar de la entidad, hacerlo en el servicio
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

    // CONSTRUCTOR PARA CREAR NUEVO ARTICULO DESDE 0 Y ABIERTO
    /*/ TODO agregar las siguientes validaciones:
    minimoUsuarios > 0, < maximoUsuarios
    maximoUsuarios > 0 > minimoUsuarios
    deadline > hoy
    /*/

    public Articulo(String nombre, String imagen, String link, String usuarioRecibe, Usuario propietario,
                    Date deadline, Double costo, TipoCosto tipoCosto, Integer minimoUsuarios, Integer maximoUsuarios) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.link = link;
        this.usuarioRecibe = usuarioRecibe;
        this.propietario = propietario;
        this.estado = EstadoArticulo.OPEN;
        this.deadline = deadline;
        this.fechaDeCreacion = new Date();
        this.anotaciones = new ArrayList<>();
        this.cantidadDeAnotaciones = 0;
        this.costo = costo;
        this.tipoCosto = tipoCosto;
        this.minimoUsuarios = minimoUsuarios;
        this.maximoUsuarios = maximoUsuarios;
    }

    public Articulo(String nombre, String imagen, String link, String usuarioRecibe, EstadoArticulo estado,
                    Date deadline, Usuario propietario, Double costo, TipoCosto tipoCosto, Integer minimoUsuarios,
                    Integer maximoUsuarios) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.link = link;
        this.usuarioRecibe = usuarioRecibe;
        this.estado = estado;
        this.deadline = deadline;
        this.propietario =propietario;
        this.fechaDeCreacion = new Date();
        this.anotaciones = new ArrayList<>();
        this.cantidadDeAnotaciones = 0;
        this.costo = costo;
        this.tipoCosto = tipoCosto;
        this.minimoUsuarios = minimoUsuarios;
        this.maximoUsuarios = maximoUsuarios;
    }

    // TODO sacar de acá, poner en el repo
    public void actualizarValores(Articulo other) {
        if (other.getNombre() != null) {
            this.setNombre(other.getNombre());
        }
        if (other.getImagen() != null) {
            this.setImagen(other.getImagen());
        }
        if (other.getLink() != null) {
            this.setLink(other.getLink());
        }
        if (other.getUsuarioRecibe() != null) {
            this.setUsuarioRecibe(other.getUsuarioRecibe());
        }
        if (other.getEstado() != null) {
            this.setEstado(other.getEstado());
        }
        if (other.getDeadline() != null) {
            this.setDeadline(other.getDeadline());
        }
        if (other.getPropietario() != null) {
            this.setPropietario(other.getPropietario());
        }
        if (other.getCosto() != null) {
            this.setCosto(other.getCosto());
        }
        if (other.getTipoCosto() != null) {
            this.setTipoCosto(other.getTipoCosto());
        }
        if (other.getMinimoUsuarios() != null) {
            this.setMinimoUsuarios(other.getMinimoUsuarios());
        }
        if (other.getMaximoUsuarios() != null) {
            this.setMaximoUsuarios(other.getMaximoUsuarios());
        }
    }
}
