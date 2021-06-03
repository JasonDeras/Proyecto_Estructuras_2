
public class Campo {
    String tipo;
    String nombre;
    

    public Campo(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        
        
    }

    public Campo() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    @Override
    public String toString() {
        return  nombre;
    }
}
