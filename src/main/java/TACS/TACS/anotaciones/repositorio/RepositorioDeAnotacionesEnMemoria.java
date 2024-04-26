package TACS.TACS.anotaciones.repositorio;

import TACS.TACS.anotaciones.Anotacion;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeAnotacionesEnMemoria implements RepositorioDeAnotaciones{

    private final List<Anotacion> anotaciones = new ArrayList<>();
    private Integer clave = 0;

    @Override
    public List<Anotacion> listarAnotaciones() {
        return anotaciones;
    }

    @Override
    public Anotacion obtenerAnotacion(Integer id) {
        return anotaciones.stream().filter(art -> art.getId().equals(id)).findFirst().get();
    }

    @Override
    public Integer guardarAnotacion(Anotacion anotacion) {
        clave+=1;
        anotacion.setId(clave);
        return clave;
    }

    // TODO revisar si es necesario
    @Override
    public void actualizarAnotacion(int id, Anotacion anotacion) {
//        Anotacion anotacion1 = this.obtenerAnotacion(id);
    }

    @Override
    public void borrarAnotaciones() {

    }
}
