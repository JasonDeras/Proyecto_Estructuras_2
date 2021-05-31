import java.io.Serializable;
import java.util.ArrayList;

public class Registro implements Serializable{
    int key;
    long byteOffset;
    
    public Registro() {
    }
 
    public Registro(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public long getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(long byteOffset) {
        this.byteOffset = byteOffset;
    }
    
    @Override
    public String toString() {
        return key +"-";
    }
    
}