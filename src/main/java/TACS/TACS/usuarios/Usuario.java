package TACS.TACS.usuarios;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    public Usuario(String nombre, String apellido, String mail){
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail=mail;
    }
    public void actualizarValores(Usuario other) {
        if (other.getNombre() != null) {
            this.setNombre(other.getNombre());
        }
        if (other.getApellido() != null) {
            this.setApellido(other.getApellido());
        }
        if (other.getMail() != null) {
            this.setMail(other.getMail());
        }
    }
}
