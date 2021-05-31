import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.DateFormat;

public class Archivo {

    private RandomAccessFile file;

    public Archivo() {

    }

    public Archivo(RandomAccessFile file) {
        this.file = file;
    }

    // Apertura del fichero
    public void abrir(AccesoCampo r)
            throws IOException {
        file = new RandomAccessFile("MetaData.dat", "rw");
    }

    public void escribir(AccesoCampo registro, Campos c) throws IOException {
        if (file != null) {
            registro.writeCampo(file, c);
        }
    }

    public void readC(AccesoCampo reg) throws IOException, ParseException {
        reg.readCampos(file);

    }

    public void cerrar()
            throws IOException {
        if (file != null) {
            file.close();
        }
    }

    public long File_size() throws IOException {

        return file.length();
    }
    public void modificarC(AccesoCampo c,Campos p) throws IOException{
        c.modificarCampo(file,p.size_dec
                ,p);
    }

}
