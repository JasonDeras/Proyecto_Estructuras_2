public class LinkedList {

    Node head;

    class Node {

        Data reg;
        int data;
        long posicion;
        Node prev;
        Node next; 
        
        Node(int d,long pos) {
            data = d;
            posicion=pos;
        }
    }

    public void BestFit(int new_data,long pos) {
        Node new_node = new Node(new_data,pos);
        Node last = head;
        new_node.next = null;
        boolean insertado = false;
        if (head == null) {

            new_node.prev = null;
            head = new_node;

        } else {
            if (new_data < last.data) {

                new_node.prev = last.prev;
                new_node.next = last;
                last.prev = new_node;
                head = new_node;
                insertado = true;
            } else {
                while (last.next != null) {

                    if (new_data < last.next.data) {
                        new_node.prev = last;
                        new_node.next = last.next;
                        last.next.prev = new_node;
                        last.next = new_node;

                        insertado = true;
                        break;
                    } else {
                        last = last.next;
                    }
                }
                if (!insertado) {

                    last.next = new_node;
                    new_node.prev = last;
                }
            }

        }
    }

    public Node SearchSpace(int needed) {
        Node last = head;
        boolean found = false;
        while (last.next != null) {
            if (needed > last.data) {
                last = last.next;
            } else {
                found = true;
                break;
            }
        }
        if (found == false) {
            if (needed > last.data) {
                return null;
            } else {
                return last;
            }
        } else {
            return last;
        }

    }

    void InsertEnd(int new_data,long pos) {

        Node new_node = new Node(new_data,pos);
        Node last = head;
        new_node.next = null;

        if (head == null) {
            new_node.prev = null;
            head = new_node;
            return;
        }

        while (last.next != null) {
            last = last.next;
        }
        last.next = new_node;

        new_node.prev = last;
    }

    public void ImprimeListaEnlazada(Node node) {
        Node last = null;
        System.out.println("Direccion Normal:");
        while (node != null) {
            System.out.print("Size: "+node.data +"Pos: "+node.posicion +" ||");
            last = node;
            node = node.next;
        }
        System.out.println();
        System.out.println("Direccion Opuesta:");
        while (last != null) {
            System.out.print(last.data + " ");
            last = last.prev;
        }
    }

    void deleteNode(Node head_ref, Node delete) {

        if (head == null || delete == null) {
            return;
        }

        if (head == delete) {
            head = delete.next;
        }
        if (delete.next != null) {
            delete.next.prev = delete.prev;
        }
        if (delete.prev != null) { 
            delete.prev.next = delete.next;
        }
        return;
    }

}