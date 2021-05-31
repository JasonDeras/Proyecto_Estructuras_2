import java.io.Serializable;
import java.util.Queue;

public class BTree implements Serializable {

    BNode root;
    int t = 3;

    BTree() {
        root = null;

    }

    void traverse() {
        if (root != null) {
            root.traverse();
            System.out.println("");
        }
    }

    void PrintLevels() {
        try{
            if (root != null) {
                for (int i = 0; i < root.n; i++) {
                    if (root.key[i] != null) {
                        System.out.print(root.key[i] + "  ");
                    }
                }

                System.out.println("");
                if (root.hijos != null) {
                    for (int i = 0; i < root.hijos.length; i++) {
                        if (root.hijos[i] != null) {
                            for (int j = 0; j < root.hijos[i].n; j++) {
                                if (root.hijos[i].key[j] != null) {
                                    System.out.print(root.hijos[i].key[j]);
                                }
                            }
                            System.out.print("|");
                        }

                    }
                    System.out.println("");
                    for (int i = 0; i < root.hijos.length; i++) {
                        if (root.hijos[i] != null) {
                            for (int j = 0; j < root.hijos[i].n; j++) {
                                if (root.hijos[i].hijos[j] != null) {
                                    for (int k = 0; k < root.hijos[i].hijos[j].n + 1; k++) {
                                        if (root.hijos[i].hijos[j].key[k] != null) {
                                            System.out.print(root.hijos[i].hijos[j].key[k]);

                                        }

                                    }
                                    System.out.print("|");
                                }

                            }
                            System.out.print("|");
                        }

                    }

                }
            }
            }catch(Exception x){
                
            }

    }

    BNode search(Registro k) {
        if (root == null) {
            return null;
        } else {
            return root.search(k);
        }
    }

    void insert(Registro r) {
        
        if (root == null) {
            root = new BNode(t, true);
            root.key[0] = r;
            root.n = 1;
        } else {
            if (root.getN() == 2 * t - 1) {

                BNode s = new BNode(t, false);

                s.hijos[0] = root;

                s.splitChild(0, root);

                int i = 0;
                if (s.getKey(0).key < r.getKey()) {
                    i++;
                }
                s.getHijos()[i].insertNonFull(r);
                root = s;
            } else {
                root.insertNonFull(r);
            }
        }
    }

    void remove(Registro k) {
        if (root == null) {
            System.out.println("El Arbol esta vacio");

            return;
        }

        root.remove(k);

        if (root.n == 0) {
            BNode tmp = root;
            if (root.isLeaf()) {
                root = null;
            } else {
                root = root.hijos[0];
            }

            tmp = null;
        }
        return;
    }

}