package TACS.TACS.articulos.repositorios;

import TACS.TACS.articulos.Articulo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioDeArticulosEnMemoria implements RepositorioDeArticulos{

    private static final List<Articulo> articulos = new ArrayList<>();

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
    public void actualizarArticulo(int Id, Articulo other) {
        Articulo artoriginal = articulos.stream().filter(art -> art.getId().equals(Id)).findFirst().get();
        if (other.getNombre() != null) {
            artoriginal.setNombre(other.getNombre());
        }
        if (other.getImagen() != null) {
            artoriginal.setImagen(other.getImagen());
        }
        if (other.getLink() != null) {
            artoriginal.setLink(other.getLink());
        }
        if (other.getUsuarioRecibe() != null) {
            artoriginal.setUsuarioRecibe(other.getUsuarioRecibe());
        }
        if (other.getEstado() != null) {
            artoriginal.setEstado(other.getEstado());
        }
        if (other.getDeadline() != null) {
            artoriginal.setDeadline(other.getDeadline());
        }
        if (other.getPropietario() != null) {
            artoriginal.setPropietario(other.getPropietario());
        }
        if (other.getCosto() != null) {
            artoriginal.setCosto(other.getCosto());
        }
        if (other.getTipoCosto() != null) {
            artoriginal.setTipoCosto(other.getTipoCosto());
        }
        if (other.getMinimoUsuarios() != null) {
            artoriginal.setMinimoUsuarios(other.getMinimoUsuarios());
        }
        if (other.getMaximoUsuarios() != null) {
            artoriginal.setMaximoUsuarios(other.getMaximoUsuarios());
        }
    }

    @Override
    public void borrarArticulos(){
        articulos.clear();
        clave = 0;
    }
}
