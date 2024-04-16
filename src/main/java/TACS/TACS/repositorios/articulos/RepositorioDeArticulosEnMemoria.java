package TACS.TACS.repositorios.articulos;

import TACS.TACS.articulos.Articulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioDeArticulosEnMemoria implements RepositorioDeArticulos{

    private static List<Articulo> articulos = new ArrayList<>();

    private static Integer clave = 0;

    @Override
    public List<Articulo> listarArticulos() {
        return new ArrayList<>(articulos);
    }

    @Override
    public Articulo obtenerArticulo(Integer id) {
        return articulos.stream().filter(art -> art.getId().equals(id)).findFirst().get();
    }

    @Override
    public Integer guardarArticulo(Articulo articulo) {
        clave++;
        articulo.setId(clave);
        articulos.add(articulo);
        return clave;
    }

    @Override
    public void actualizarArticulo(int Id, Articulo articulo) {
        Articulo artoriginal = articulos.stream().filter(art -> art.getId().equals(Id)).findFirst().get();
        artoriginal.actualizarValores(articulo);
    }
    public void borrarArticulos(){
        articulos.clear();
        clave = 0;
    }
}
