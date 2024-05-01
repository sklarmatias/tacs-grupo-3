package TACS.TACS.usuarios;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Setter
    private Integer id;

    private String nombre;

    private String apellido;

    private String mail;

    private List<Articulo> articulosPublicados;

    private List<Anotacion> anotaciones;

    public boolean interactuo(){
        return !(this.articulosPublicados.isEmpty() && this.anotaciones.isEmpty());
    }

    public Usuario(String nombre, String apellido, String mail){
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail=mail;
        this.anotaciones = new ArrayList<>();
        this.articulosPublicados = new ArrayList<>();
    }

    public void agregarAnotacion(Anotacion anotacion){
        this.getAnotaciones().add(anotacion);
    }

    public UsuarioDTO convertirADTO(){
        return new UsuarioDTO(this);
    }

    public static class UsuarioDTO{

        public Integer id;

        public String nombre;

        public String apellido;

        public String email;

        public UsuarioDTO(Usuario usuario){
            this.id = usuario.getId();
            this.nombre = usuario.getNombre();
            this.apellido = usuario.getApellido();
            this.email = usuario.getMail();
        }
    }

}
