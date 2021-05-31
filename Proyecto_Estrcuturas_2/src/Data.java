import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable{
    long ubicacion;
    int key;
    String size_alter;
    ArrayList<Object> datos=new ArrayList<>();

    public Data() {
        size_alter="|";
    }

    
    public long getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(long ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }


    public String getSize_alter() {
        return size_alter;
    }

    public void setSize_alter(String size_alter) {
        this.size_alter = size_alter;
    }

    public ArrayList<Object> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<Object> datos) {
        this.datos = datos;
        key=(int)datos.get(0);
    }

    @Override
    public String toString() {
        return "Data{" + "ubicacion=" + ubicacion + ", key=" + key +'}';
    }   

}