package TACS.TACS.repositorios.articulos;

import TACS.TACS.articulos.Articulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioDeArticulosEnMemoria implements RepositorioDeArticulos{

    private static Map<Integer, Articulo> articulos = new HashMap<>();

    private static Integer clave = 0;

    @Override
    public List<Articulo> listarArticulos() {
        return new ArrayList<>(articulos.values());
    }

    @Override
    public Articulo obtenerArticulo(Integer id) {
        return articulos.get(id);
    }

    @Override
    public Integer guardarArticulo(Articulo articulo) {
        clave += 1;
        articulos.put(clave, articulo);
        return clave;
    }

    @Override
    public void actualizarArticulo(Articulo articulo) {
        articulos.put(articulo.getId(), articulo);
    }
}
