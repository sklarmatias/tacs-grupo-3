package TACS.TACS.anotaciones.repositorio;

import TACS.TACS.anotaciones.Anotacion;

import java.util.List;

public interface RepositorioDeAnotaciones {

    List<Anotacion> listarAnotaciones();

    Anotacion obtenerAnotacion(Integer id);

    Integer guardarAnotacion(Anotacion anotacion);

    void actualizarAnotacion(int Id, Anotacion anotacion);

    void borrarAnotaciones();

}
