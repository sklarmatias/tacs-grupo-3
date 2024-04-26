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

    @Setter
    private Integer id;

    private Usuario usuario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fecha;

    public Anotacion(Usuario usuario){
        this.usuario = usuario;
        this.fecha = new Date();
    }

}
