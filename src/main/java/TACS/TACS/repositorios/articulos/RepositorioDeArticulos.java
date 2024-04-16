package TACS.TACS.repositorios.articulos;

import TACS.TACS.articulos.Articulo;

import java.util.List;

public interface RepositorioDeArticulos {

    List<Articulo> listarArticulos();

    Articulo obtenerArticulo(Integer id);

    Integer guardarArticulo(Articulo articulo);

    void actualizarArticulo(int Id, Articulo articulo);

}
