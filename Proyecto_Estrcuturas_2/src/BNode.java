
import java.io.Serializable;


public class BNode implements Serializable{

    Registro[] key;
    int t;     
    BNode[] hijos; 
    int n;      
    boolean leaf; 

    public BNode(int _t, boolean _leaf) {
        t = _t;
        leaf = _leaf;
        key = new Registro[2 * t - 1];
        hijos = new BNode[2 * t];

    }

    public Registro[] getKeys() {
        return key;
    }

    public void addregistro(Registro r, int pos) {
        key[pos] = r;
    }

    public void setKeys(Registro[] keys) {
        this.key = keys;
    }

    public Registro getKey(int pos) {
        return key[pos];
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public BNode[] getHijos() {
        return hijos;
    }

    public void setHijos(BNode[] hijos) {
        this.hijos = hijos;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    void traverse() {
        int i;
        for (i = 0; i < n; i++) {
            if (leaf == false) 
            {
                hijos[i].traverse();
            }
            System.out.print(", " + key[i]);
        }

        if (leaf == false) {
            hijos[i].traverse();
        }
    }


    BNode search(Registro k) {
        int i = 0;
 
        for (i = 0; i < n; i++) {
            if (key[i].getKey() >= k.getKey()) {
                break;
            }
        }
        if (i < n && key[i].getKey() == k.getKey()) {
            return this;
        }
        if (leaf == true) {
            return null;
        }
        return hijos[i].search(k);
    }

    void insertNonFull(Registro k) {
        int i = n - 1;
        if (leaf == true) {
            while (i >= 0 && key[i].getKey() > k.getKey()) {
                key[i + 1] = key[i];
                i--;
            }

            key[i + 1] = k;
            n = n + 1;
        } else {
            while (i >= 0 && key[i].getKey() > k.getKey()) {
                i--;
            }

            if (hijos[i + 1].getN() == 2 * t - 1) {
                splitChild(i + 1, hijos[i + 1]);

                if (key[i + 1].getKey() < k.getKey()) {
                    i++;
                }
            }
            hijos[i + 1].insertNonFull(k);
        }

    }

    void splitChild(int i, BNode y) {

        BNode z = new BNode(y.getT(), y.isLeaf());
        z.setN(t - 1);

        for (int j = 0; j < t - 1; j++) 
        {
            z.key[j] = y.key[j + t];
        }

        if (y.leaf == false) {
            for (int j = 0; j < t; j++) 
            {
                z.hijos[j] = y.hijos[j + t];
            }
        }

        y.n = t - 1;

        for (int j = n; j >= i + 1; j--) 
        {
            hijos[j + 1] = hijos[j];
        }

        hijos[i + 1] = z;

        for (int j = n - 1; j >= i; j--)
        {
            key[j + 1] = key[j];
        }

        key[i] = y.key[t - 1];

        n = n + 1;
    }

    public int findKey(Registro k) {
        int idx = 0;
        while ((idx < n) && (key[idx].getKey() < k.getKey())) {
            ++idx;
        }
        return idx;
    }

    void remove(Registro k) {
        int idx = findKey(k);

        if (idx < n && key[idx].getKey() == k.getKey()) {

            if (leaf) {
                removeFromLeaf(idx);
            } else {
                removeFromNonLeaf(idx);
            }
        } else {

            if (leaf) {
                System.out.println("El Registro [ " + k.getKey() + " ] no existe en el Arbol");
                return;
            }

            boolean bandera = ((idx == n) ? true : false);

            if (hijos[idx].getN() < t) {
                fill(idx);
            }

            if (bandera && idx > n) {
                hijos[idx - 1].remove(k);
            } else {
                hijos[idx].remove(k);
            }
        }
        return;
    }

    void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; ++i) {
            key[i - 1] = (key[i]);
        }
        n--;

        return;
    }

    void removeFromNonLeaf(int idx) {
        Registro k = key[idx];

        if (hijos[idx].getN() >= t) {
            Registro pred = getPred(idx);
            key[idx] = pred;
            hijos[idx].remove(pred);
        }else if (hijos[idx + 1].n >= t) {
            Registro succ = getSucc(idx);
            key[idx] = succ;
            hijos[idx + 1].remove(succ);
        }else {
            merge(idx);
            hijos[idx].remove(k);
        }
        return;
    }

    Registro getPred(int idx) {
        BNode cur = hijos[idx];
        while (!cur.isLeaf()) {
            cur = cur.hijos[cur.n];
        }
        return cur.key[cur.n - 1];
    }

    Registro getSucc(int idx) {

        BNode cur = hijos[idx + 1];
        while (!cur.leaf) {
            cur = cur.hijos[0];
        }
        return cur.key[0];
    }

    void fill(int idx) {

        if (idx != 0 && hijos[idx - 1].n >= t) {
            borrowFromPrev(idx);
        }else if (idx != n && hijos[idx + 1].n >= t) {
            borrowFromNext(idx);
        }else {
            if (idx != n) {
                merge(idx);
            } else {
                merge(idx - 1);
            }
        }
        return;
    }

    void borrowFromPrev(int idx) {

        BNode child = hijos[idx];
        BNode sibling = hijos[idx - 1];

        for (int i = child.n - 1; i >= 0; --i) {
            child.key[i + 1] = child.key[i];
        }

        if (!child.isLeaf()) {
            for (int i = child.getN(); i >= 0; --i) {
                child.hijos[i + 1] = child.hijos[i];
            }
        }

        child.key[0] = key[idx - 1];

        if (!child.isLeaf()) {
            child.hijos[0] = sibling.hijos[sibling.n];
        }

        key[idx - 1] = sibling.key[sibling.n - 1];

        child.n += 1;
        sibling.n -= 1;

        return;
    }

    void borrowFromNext(int idx) {

        BNode child = hijos[idx];
        BNode sibling = hijos[idx + 1];

        child.key[(child.getN())] = key[idx];

        if (!(child.isLeaf())) {
            child.hijos[(child.getN()) + 1] = sibling.hijos[0];
        }

        key[idx] = sibling.key[0];

        for (int i = 1; i < sibling.getN(); ++i) {
            sibling.key[i - 1] = sibling.key[i];
        }

        if (!sibling.isLeaf()) {
            for (int i = 1; i <= sibling.getN(); ++i) {
                sibling.hijos[i - 1] = sibling.hijos[i];
            }
        }

        child.n += 1;
        sibling.n -= 1;

        return;
    }

    void merge(int idx) {
        BNode child = hijos[idx];
        BNode sibling = hijos[idx + 1];

        child.key[t - 1] = key[idx];

        for (int i = 0; i < sibling.n; ++i) {
            child.key[i + t] = sibling.key[i];
        }

        if (!child.isLeaf()) {
            for (int i = 0; i <= sibling.n; ++i) {
                child.hijos[i + t] = sibling.hijos[i];
            }
        }

        for (int i = idx + 1; i < n; ++i) {
            key[i - 1] = key[i];
        }

        for (int i = idx + 2; i <= n; ++i) {
            hijos[i - 1] = hijos[i];
        }

        child.n += sibling.n + 1;
        n--;

        sibling=null;
        return;
    }

    @Override
    public String toString() {
        String a = "";
        for (int i = 0; i < n; i++) {
            if (key[i] != null) {
                a += key[i].toString() + ",";
            }
        }
        return "BNode{" + a + '}';
    }

}
