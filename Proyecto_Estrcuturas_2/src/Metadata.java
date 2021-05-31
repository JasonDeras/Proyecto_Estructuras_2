import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

public class Metadata implements Serializable {

    
    private String nombre;
    private ArrayList campos;
    private ArrayList tipos;
    private int numregistros = 0;
    private long Campos_en_Archivo;
    BTree ArbolB;
    private int sizeMeta;

    public BTree getArbolB() {
        return ArbolB;
    }

    public void setArbolB(BTree ArbolB) {
        this.ArbolB = ArbolB;
    }

    public Metadata() {
       ArbolB = new BTree();
    }

    public int getSizeMeta() {
        return sizeMeta;
    }

    public void setSizeMeta(int sizeMeta) {
        this.sizeMeta = sizeMeta;
    }
    
    public ArrayList getTipos() {
        return tipos;
    }

    public void setTipos(ArrayList tipos) {
        this.tipos = tipos;
    }

    public int getNumregistros() {
        return numregistros;
    }

    public void setNumregistros(int numregistros) {
        this.numregistros = numregistros;
    }

    public void addnumregistros() {
        this.numregistros++;
    }
    public void subtractnumregistros(){
        this.numregistros--;
    }

    public ArrayList getCampos() {
        return campos;
    }

    public void setCampos(ArrayList campos) {
        this.campos = campos;

    }

    public Metadata(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCampos_en_Archivo() {
        return Campos_en_Archivo;
    }

    public void setCampos_en_Archivo(long Campos_en_Archivo) {
        this.Campos_en_Archivo = Campos_en_Archivo;
    }
    
    @Override
    public String toString() {
        return "Metadata{" + "nombre=" + nombre + '}';
    }

}