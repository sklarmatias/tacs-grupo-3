package TACS.TACS.anotaciones;

import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@NoArgsConstructor
public class Anotacion {

    private Usuario usuario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fecha;

    public Anotacion(Usuario usuario){
        this.usuario = usuario;
        this.fecha = new Date();
        usuario.agregarAnotacion(this);
    }

    public AnotacionDTO convertirADTO(){
        return new AnotacionDTO(this);
    }

    public static class AnotacionDTO{

        public Usuario.UsuarioDTO usuario;

        public Date fecha;

        public AnotacionDTO(Anotacion anotacion){
            usuario = anotacion.getUsuario().convertirADTO();
            fecha = anotacion.getFecha();
        }

    }

}
