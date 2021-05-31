import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;

public class AccesoCampo  extends Archivo {

    public int size;
    public long numCampos;
    public ArrayList<Campos> Camps = new ArrayList<>();

    public AccesoCampo() {
        numCampos = 0;
    }

    public AccesoCampo(int size, int numCampos) {
        this.size = size;
        this.numCampos = numCampos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getNumCampos() {
        return numCampos;
    }

    public void setNumCampos(int numCampos) {
        this.numCampos = numCampos;
    }

    public ArrayList<Campos> getCamps() {
        return Camps;
    }

    public void setCamps(ArrayList<Campos> Camps) {
        this.Camps = Camps;
    }

    public Campos getCampo(int x) {
        return Camps.get(x);
    }

    public void addCampo(Campos e) {
        Camps.add(e);
    }

    public void readCampos(RandomAccessFile file)
            throws IOException, java.text.ParseException {
        boolean registro_elimando = false;
        file.seek(0);
        numCampos = file.readLong();
        file.skipBytes(2);

        for (int j = 0; j < numCampos; j++) {
            boolean frase_encontrada = false;
            long ubicacion = file.readLong();

            System.out.print(ubicacion);
            System.out.print((char) file.readByte());
            long size = file.readLong();
            System.out.print(size);
            System.out.print((char) file.readByte());
            String nombre = "";
            String acumulador = "";
            Campos c = new Campos();

            while (frase_encontrada == false) {
                acumulador = "";
                acumulador += (char) file.readByte();
                if (acumulador.contains("|")) {
                    frase_encontrada = true;
                } else {
                    nombre += acumulador;
                }

            }
            System.out.print(nombre + acumulador);
            int tipo = file.readInt();
            System.out.print(tipo);
            System.out.print((char) file.readByte());
            boolean key = file.readBoolean();
            System.out.print(key);
            file.skipBytes(2);
            System.out.println("");
            c.setKey(key);
            c.agregarNombre(nombre);
            c.setFieldType(tipo);
            c.setSizeBytes(size);
            c.setSize_dec(ubicacion);

            Camps.add(c);

            Camps.add(c);
        }
    }

    private String readString(RandomAccessFile file) throws IOException {
        char campo[] = new char[size];
        for (int i = 0; i < size; i++) {

            campo[i] = file.readChar();

        }

        return new String(campo).replace('\0', ' ');
    }

    public void writeCampo(RandomAccessFile file, Campos c) throws IOException {
        file.seek(0);
        if (file.length() == 0) {
            file.writeLong(0);
            file.writeBytes(System.getProperty("line.separator"));
        }

        file.seek(file.length());
        String name = "";
        long size_total = c.sizeBytes;
        file.writeLong(file.length());
        file.writeBytes("|");
        file.writeLong(size_total);
        file.writeBytes("|");
        for (int i = 0; i < c.cadena.length(); i++) {
            char string[] = c.getName();
            file.writeByte(string[i]);
        }
        file.writeBytes("|");
        file.writeInt(c.FieldType);
        file.writeBytes("|");
        file.writeBoolean(c.key);
        file.writeBytes(System.getProperty("line.separator"));
        numCampos++;
        file.seek(0);
        file.writeLong(numCampos);
    }

    public void modificarCampo(RandomAccessFile file, long ubicacion,Campos c) throws IOException {
        file.seek(ubicacion);
        file.skipBytes(8 + 1 + 8 + 1);
        for (int i = 0; i < c.cadena.length(); i++) {
            char string[] = c.getName();
            file.writeByte(string[i]);
        }
    }
}
