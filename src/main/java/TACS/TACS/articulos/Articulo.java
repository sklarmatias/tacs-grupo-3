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

    public void anotarUsuario(Usuario usuario){
        if (!this.estaAbierto())
            throw new IllegalArgumentException("El articulo está cerrado.");
        if (this.estaCompleto())
            throw new IllegalArgumentException("El articulo llegó a la cantidad máxima de usuarios anotados.");
        if (this.getPropietario() == usuario)
            throw new IllegalArgumentException("El propietario del artículo no se puede anotar a su propio artículo.");
        Anotacion anotacion = new Anotacion(usuario);
        this.anotaciones.add(anotacion);
    }

    public boolean estaCompleto(){
        return this.cantidadDeAnotaciones >= this.maximoUsuarios;
    }

    public boolean estaAbierto(){
        return this.estado == EstadoArticulo.OPEN;
    }

    public boolean estaVencido(){
        return new Date().after(this.deadline);
    }

    public void cerrar(){
        // TODO notificar a los usuarios
        if (this.cantidadDeAnotaciones > this.minimoUsuarios)
            this.estado = EstadoArticulo.CLOSED_SUCCESS;
        else this.estado = EstadoArticulo.CLOSED_FAILED;
    }

    public boolean fueCerradoConExito(){
        return this.estado.equals(EstadoArticulo.CLOSED_SUCCESS);
    }

    public ArticuloDTO convertirADTO(){
        return new Articulo.ArticuloDTO(this);
    }

    public static class ArticuloDTO{

        public Integer id;

        public String nombre;

        public String imagen;

        public String link;

        public String usuarioRecibe;

        public EstadoArticulo estado;

        // TODO sacar de la entidad, hacerlo en el servicio
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date deadline;

        public Usuario.UsuarioDTO propietario;

        // TODO sacar de la entidad, hacerlo en el servicio
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date fechaDeCreacion;

        public List<Anotacion> anotaciones;

        public Integer cantidadDeAnotaciones;

        public Double costo;

        public TipoCosto tipoCosto;

        public Integer minimoUsuarios;

        public Integer maximoUsuarios;

        public ArticuloDTO(Articulo articulo){

            this.id = articulo.getId();
            this.nombre = articulo.getNombre();
            this.imagen = articulo.getImagen();
            this.link = articulo.getLink();
            this.usuarioRecibe = articulo.getUsuarioRecibe();
            this.estado = articulo.getEstado();
            this.deadline = articulo.getDeadline();
            this.fechaDeCreacion = articulo.getFechaDeCreacion();
            this.cantidadDeAnotaciones = articulo.getCantidadDeAnotaciones();
            this.costo = articulo.getCosto();
            this.tipoCosto = articulo.getTipoCosto();
            this.maximoUsuarios = articulo.getMaximoUsuarios();
            this.minimoUsuarios = articulo.getMinimoUsuarios();
            this.propietario = articulo.getPropietario().convertirADTO();
            this.anotaciones = articulo.getAnotaciones();
        }

    }

}
