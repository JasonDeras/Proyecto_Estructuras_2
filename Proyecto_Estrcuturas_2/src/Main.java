
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.CellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;
import org.apache.xmlbeans.StringEnumAbstractBase.Table;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Main extends javax.swing.JFrame {

    int contador = 0;
    ArrayList<Campo> listcampos = new ArrayList();

    public void Salvar_Archivo() {
        JOptionPane.showMessageDialog(null, "Su file se ha guardado exitosamente! ...Always On Saving!");
    }

    public void Cargar_Archivo() {
        FileSuccess = 0;
        String direction;

        //Creo un nuevo JFileChooser para que eliga donde guardar.
        //Le digo que aparezca en el home del proyecto .. Crea un problema que la Metadata se puede guardar en cualquier sitio.
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        FileNameExtensionFilter data = new FileNameExtensionFilter("DAT FILE", "dat");
        fileChooser.setFileFilter(data);
        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) { //Cuando le da guardar
            //System.out.println(fileChooser.getCurrentDirectory().toString());
            File file = null;
            // FileOutputStream fos = null;
            // ObjectOutputStream ous = null;
            try {
                if (fileChooser.getFileFilter().getDescription().equals("DAT FILE")) { //Chequea si lo que quiere guardar es DAT FILE
                    direction = fileChooser.getSelectedFile().getPath() + ".dat";
                    file = fileChooser.getSelectedFile();
                    this.file = file;
                    JOptionPane.showMessageDialog(null, "Sucess!");
                    System.out.println("Length of Loaded File: " + (file.length() - 4)); //SIZE MENOS BUFFER.
                    FileSuccess = 1;
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to Load. Use DAT FILE.");
                }
                // fos = new FileOutputStream(file);
                //  ous = new ObjectOutputStream(fos);
                //  ous.flush(); //Lo oficializo

                // RAfile=new RandomAccessFile(file,"rw");
            } catch (Exception e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Something Went Wrong! Contact System Administrator.");
            }
            try {
                //ous.close();
                // fos.close();
            } catch (Exception e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Fatal error closing files.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Operation aborted!");
        }

    }

    private void BuildTable(Metadata metadata, int funcion) {
        if (funcion == 0) {
            Object[] campos = metadata.getCampos().toArray();
            DefaultTableModel tabla = new DefaultTableModel();
            tabla.setColumnCount(campos.length);

            tabla.setColumnIdentifiers(campos);
            Table.setModel(tabla);
        } else if (funcion == 1) {
            Table.setModel(cleanTable);
        }

    }

    public void CargarMetadatos() throws ClassNotFoundException {
        try {
            RAfile = new RandomAccessFile(file, "rw");
            int tamaño = RAfile.readInt();
            byte[] data = new byte[tamaño];
            RAfile.read(data);
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream read = new ObjectInputStream(in);
            metadata = (Metadata) read.readObject();
            metadata.setSizeMeta(tamaño);
        } catch (IOException ex) {
        }
    }

    public void LeerDatosRegistro() throws ClassNotFoundException {

        try {

            RAfile = new RandomAccessFile(file, "rw");
            RAfile.seek(0);
            int tamaño = RAfile.readInt();
            RAfile.seek(tamaño + 4);

            boolean eliminado = false;

            while (RAfile.getFilePointer() < RAfile.length()) {
                eliminado = false;
                tamaño = RAfile.readInt();
                byte[] data = new byte[tamaño];
                RAfile.read(data);
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream read = new ObjectInputStream(in);
                Data d = (Data) read.readObject();
                if (d.getSize_alter().contains("*")) {
                    eliminado = true;
                    AvailList.BestFit(tamaño, d.ubicacion);

                } else {
                    Export2 = new ArrayList<>();
                    Registro temporal = new Registro(d.getKey());
                    temporal.setByteOffset(d.getUbicacion());
                    metadata.getArbolB().insert(temporal);
                    for (int i = 0; i < d.getDatos().size(); i++) {
                        Export2.add(d.getDatos().get(i));

                    }
                    Table_Insert_Registro();

                }

            }
            metadata.ArbolB.traverse();
            metadata.ArbolB.PrintLevels();
        } catch (IOException ex) {
        }
    }

    private void Nuevo_Archivo() {

        String direction;
        int option = JOptionPane.showConfirmDialog(this, "Desea Salvar su Proceoso?");

        if (option == JOptionPane.NO_OPTION) {
            Crear_Archivo();
            if (FileSuccess == 1) {
                metadata = new Metadata();
                BuildTable(metadata, 1);
            }

        } else if (option == JOptionPane.YES_OPTION) {
            Salvar_Archivo();
        } else {
        }
    }

    private void Crear_Archivo() {

        FileSuccess = 0;
        String direction;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        FileNameExtensionFilter data = new FileNameExtensionFilter("DAT FILE", "dat");
        fileChooser.setFileFilter(data);
        int seleccion = fileChooser.showSaveDialog(this);

        if (seleccion == JFileChooser.APPROVE_OPTION) {

            File file = null;
            FileOutputStream fos = null;
            ObjectOutputStream ous = null;

            try {
                if (fileChooser.getFileFilter().getDescription().equals("DAT FILE")) {
                    direction = fileChooser.getSelectedFile().getPath().toString() + ".dat";
                    direction = direction.replace(".dat", "");
                    direction += ".dat";

                    file = new File(direction);
                    if (file.length() == 0) {
                        this.file = new File(direction);
                        JOptionPane.showMessageDialog(this, "Sucesso!\n Calquier progreso sin salvar se perdio");

                    } else if (file.exists()) {
                        file.delete();
                        file.createNewFile();
                        this.file = new File(direction);
                        JOptionPane.showMessageDialog(this, "File OverWritten, New Length: " + file.length());
                    }
                    FileSuccess = 1;
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to save. Use DAT FILE.");
                }
                fos = new FileOutputStream(file);
                ous = new ObjectOutputStream(fos);
                ous.flush();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Algo salio mal");
            }
            try {
                ous.close();
                fos.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cerrando Archivos.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Operation aborted!");
        }
    }

    public void Escribir_Metadatos() throws IOException {

        RAfile = new RandomAccessFile(file, "rw");
        ByteArrayOutputStream obArray = new ByteArrayOutputStream();
        ObjectOutputStream objeto = new ObjectOutputStream(obArray);
        objeto.writeObject(metadata);
        byte[] datos = obArray.toByteArray();
        RAfile.seek(0);
        RAfile.writeInt(datos.length);
        RAfile.write(datos);
        metadata.setSizeMeta((int) RAfile.length());

    }

    private void Crear_Registro() {

        TableModel model = Table.getModel();
        DefaultTableModel modelo = (DefaultTableModel) model;

        Object[] insertarray = new Object[metadata.getCampos().size()];
        for (int i = 0; i < metadata.getCampos().size(); i++) {
                    String temp = JOptionPane.showInputDialog(null, "Ingrese: " + metadata.getCampos().get(i).toString() + "\n Tipo:  " + metadata.getTipos().get(i).toString());
                    if (metadata.getTipos().get(i).toString().equals("Int")) {
                        insertarray[i] = Integer.parseInt(temp);
                        
                    } else if (metadata.getTipos().get(i).toString().equals("long")) {
                        insertarray[i] = Long.parseLong(temp);
                    } else if (metadata.getTipos().get(i).toString().equals("String")) {
                        insertarray[i] = temp;
                    } else if (metadata.getTipos().get(i).toString().equals("Char")) {
                        insertarray[i] = temp.charAt(0);
                    }
        }
        
        ArrayList export2 = new ArrayList();

        for (int i = 0; i < insertarray.length; i++) {
            export2.add(insertarray[i]);
        }
        Registro temporal = new Registro(Integer.parseInt(insertarray[0].toString()));

        if (metadata.getArbolB().search(temporal) == null) {
            if (Integer.parseInt(insertarray[0].toString()) >= 1 && Integer.parseInt(insertarray[0].toString()) < 100000) {
                metadata.getArbolB().insert(temporal);
                modelo.addRow(insertarray);
                metadata.addnumregistros();
                try {
                    Escribir_Datos_Registro(export2);
                    Buscar_Dato_Archivo(temporal);
                } catch (Exception ex) {
                }

                Table.setModel(modelo);
            } else {
                JOptionPane.showMessageDialog(null, "Ingrese valores entre 9999 y 100,000");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Una Instancia del Registro ya existe.");
        }

    }

    public void Escribir_Datos_Registro(ArrayList<Object> info_registro) {

        try {
            if (AvailList.head != null) {

                Data datos = new Data();
                Registro temporal = new Registro(Integer.parseInt(info_registro.get(0).toString()));
                long byteOffset = RAfile.length();
                BNode d = metadata.getArbolB().search(temporal);
                int x = searchEnNodo(d, temporal.getKey());

                d.key[x].setByteOffset(byteOffset);
                datos.setDatos(info_registro);
                datos.setUbicacion(byteOffset);

                ByteArrayOutputStream obArray = new ByteArrayOutputStream();
                ObjectOutputStream objeto = new ObjectOutputStream(obArray);
                objeto.writeObject(datos);

                byte[] dat = obArray.toByteArray();
                int required_size = dat.length;
                LinkedList.Node espacio = AvailList.SearchSpace(required_size);

                if (espacio == null) {
                    RAfile.seek(byteOffset);
                    RAfile.writeInt(dat.length);
                    RAfile.write(dat);
                } else {
                    datos.setUbicacion(espacio.posicion);
                    int j = 0;
                    for (int i = 0; i < (espacio.data - dat.length); i++) {
                        datos.setSize_alter(datos.getSize_alter() + "|");
                        j++;
                    }

                    obArray = new ByteArrayOutputStream();
                    objeto = new ObjectOutputStream(obArray);
                    objeto.writeObject(datos);
                    dat = obArray.toByteArray();
                    d.key[x].setByteOffset(datos.ubicacion);

                    RAfile.seek(datos.ubicacion);
                    RAfile.writeInt(dat.length);
                    RAfile.write(dat);
                    AvailList.deleteNode(AvailList.head, espacio);
                }
            } else {
                Data datos = new Data();
                Registro temporal = new Registro(Integer.parseInt(info_registro.get(0).toString()));
                long byteOffset = RAfile.length();
                BNode d = metadata.getArbolB().search(temporal);
                int x = searchEnNodo(d, temporal.getKey());

                d.key[x].setByteOffset(byteOffset);
                datos.setDatos(info_registro);
                datos.setUbicacion(byteOffset);

                ByteArrayOutputStream obArray = new ByteArrayOutputStream();
                ObjectOutputStream objeto = new ObjectOutputStream(obArray);
                objeto.writeObject(datos);
                byte[] dat = obArray.toByteArray();
                RAfile.seek(byteOffset);
                RAfile.writeInt(dat.length);
                RAfile.write(dat);
            }

        } catch (IOException | NumberFormatException ex) {
        }

    }

    private void Table_Insert_Registro() {

        TableModel model = Table.getModel();
        DefaultTableModel modelo = (DefaultTableModel) model;
        metadata.addnumregistros();

        Object insertArray[] = Export2.toArray();

        modelo.addRow(insertArray);

        Table.setModel(model);

    }

    public Data Buscar_Dato_Archivo(Registro r) throws IOException, ClassNotFoundException {

        if (metadata.getArbolB().search(r) != null) {
            BNode contenido = metadata.getArbolB().search(r);
            int pos = searchEnNodo(contenido, r.getKey());
            long byteOffset = contenido.key[pos].byteOffset;
            RAfile.seek(byteOffset);
            int tamaño = RAfile.readInt();
            byte[] data = new byte[tamaño];
            RAfile.read(data);
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream read = new ObjectInputStream(in);
            Data d = (Data) read.readObject();

            return d;
        } else {
            return null;
        }

    }

    public int searchEnNodo(BNode d, int key) {

        int pos = 0;
        if (d != null) {
            for (int i = 0; i < d.n; i++) {
                if (d.key[i].getKey() == key) {
                    break;
                } else {
                    pos++;
                }
            }
        } else {
        }
        return pos;
    }

    public void Eliminar_Dato_Archivo(ArrayList<Object> export) {

        try {
            Registro temporal = new Registro(Integer.parseInt(export.get(0).toString()));

            if (Buscar_Dato_Archivo(temporal) != null) {

                Data temp = Buscar_Dato_Archivo(temporal);
                RAfile.seek(temp.ubicacion);
                int size_act = RAfile.readInt();
                temp.setSize_alter("*");
                temp.size_alter = "*";
                BNode b = metadata.ArbolB.search(temporal);
                int pos = searchEnNodo(b, temporal.key);
                long ubicacion = b.key[pos].getByteOffset();
                temp.ubicacion = ubicacion;

                ByteArrayOutputStream obArray = new ByteArrayOutputStream();
                ObjectOutputStream objeto = new ObjectOutputStream(obArray);

                obArray = new ByteArrayOutputStream();
                objeto = new ObjectOutputStream(obArray);
                objeto.writeObject(temp);

                byte[] dat2 = obArray.toByteArray();
                RAfile.write(dat2);

                AvailList.BestFit(size_act, temp.ubicacion);
                AvailList.ImprimeListaEnlazada(AvailList.head);
                metadata.ArbolB.remove(temporal);

            }
        } catch (Exception ex) {
        }
    }

    public void Modificar_Dato_Archivo(ArrayList<Object> Export) {
        try {
            Registro temporal = new Registro(Integer.parseInt(Export.get(0).toString()));
            if (Buscar_Dato_Archivo(temporal) != null) {
                Data temp = Buscar_Dato_Archivo(temporal);
                temporal.setByteOffset(temp.ubicacion);
                RAfile.seek(temp.ubicacion);
                int size_act = RAfile.readInt();

                Data new_size = new Data();
                new_size.setKey((int) Export.get(0));
                new_size.setDatos(Export);
                new_size.setUbicacion(temp.getUbicacion());
                ByteArrayOutputStream obArray = new ByteArrayOutputStream();
                ObjectOutputStream objeto = new ObjectOutputStream(obArray);
                objeto.writeObject(new_size);
                byte[] dat = obArray.toByteArray();

                if (dat.length <= size_act) {
                    for (int i = 0; i < (size_act - dat.length); i++) {
                        new_size.setSize_alter(new_size.getSize_alter() + "|");
                    }

                    obArray = new ByteArrayOutputStream();
                    objeto = new ObjectOutputStream(obArray);
                    objeto.writeObject(new_size);
                    dat = obArray.toByteArray();
                    RAfile.write(dat);

                } else {
                    temp.setSize_alter("*");
                    obArray = new ByteArrayOutputStream();
                    objeto = new ObjectOutputStream(obArray);
                    objeto.writeObject(temp);
                    byte[] dat2 = obArray.toByteArray();
                    RAfile.write(dat2);

                    long byteOffset = RAfile.length();

                    new_size.setUbicacion(byteOffset);
                    obArray = new ByteArrayOutputStream();
                    objeto = new ObjectOutputStream(obArray);
                    objeto.writeObject(new_size);
                    dat = obArray.toByteArray();

                    RAfile.seek(byteOffset);
                    RAfile.writeInt(dat.length);
                    RAfile.write(dat);

                    BNode tmp = metadata.getArbolB().search(temporal);
                    int ubicacion = searchEnNodo(tmp, temp.getKey());
                    tmp.key[ubicacion].byteOffset = byteOffset;

                    AvailList.BestFit(size_act, temporal.byteOffset);
                    AvailList.ImprimeListaEnlazada(AvailList.head);

                }
            }
        } catch (Exception ex) {
        }
    }

    public static void exportXML(ArrayList Campos, ArrayList Regs, String Direccion) {

        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            document = implementation.createDocument(null, "xml", null);

            for (int i = 0; i < Regs.size(); i++) {
                Element registro = document.createElement("Registro" + i);
                document.getDocumentElement().appendChild(registro);
                ArrayList<Element> elementos = new ArrayList();

                for (int j = 0; j < Campos.size(); j++) {
                    Element campos = document.createElement(Campos.get(j).toString());
                    elementos.add(campos);
                }

                for (int h = 0; h < elementos.size(); h++) {
                    registro.appendChild(elementos.get(h));
                    Text valorCampo = document.createTextNode(Regs.get(h).toString());
                    elementos.get(h).appendChild(valorCampo);
                    document.setXmlVersion("1.0");

                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            File archivo = new File("./" + ".xml");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

        } catch (Exception e) {

        }
    }

    public Main() {
        initComponents();
        this.setTitle("Principal");
        this.setExtendedState(MAXIMIZED_BOTH);
        metadata = new Metadata();
        //Setting up table default design.
        this.setLocationRelativeTo(null);
        Table.setForeground(Color.BLACK);
        Table.setBackground(Color.WHITE);
        Table.setFont(new Font("", 1, 22));
        Table.setRowHeight(30);
        Table.putClientProperty("terminateEditOnFocusLost", true);
        cleanTable = Table.getModel();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JDMODIFICAR_CAMPOS = new javax.swing.JDialog();
        cbocampos = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnModificar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        txtnuevo_Nombre = new javax.swing.JTextField();
        cbonuevo_tipo = new javax.swing.JComboBox<>();
        Listado_de_Campos = new javax.swing.JDialog();
        Listar_Campos = new javax.swing.JScrollPane();
        Table1 = new javax.swing.JTable();
        JDCREAR_CAMPO = new javax.swing.JDialog();
        btnCrear = new javax.swing.JButton();
        btnMostrar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtcr_nombre = new javax.swing.JTextField();
        cbocr_tipo = new javax.swing.JComboBox<>();
        JDELIMINAR_CAMPOS = new javax.swing.JDialog();
        cboEliminar = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        jsp_Tabla = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        jmb_Principal = new javax.swing.JMenuBar();
        jm_Archivo = new javax.swing.JMenu();
        jmi_Nuevo_Archivo = new javax.swing.JMenuItem();
        jmi_Salvar_Archivo = new javax.swing.JMenuItem();
        jmi_Cerrar_Archivo = new javax.swing.JMenuItem();
        jmi_Cargar_Archivo = new javax.swing.JMenuItem();
        jmi_Salir = new javax.swing.JMenuItem();
        jmi_Campos = new javax.swing.JMenu();
        jmi_Crear_Campo = new javax.swing.JMenuItem();
        jmi_Modificar_Campo = new javax.swing.JMenuItem();
        jmi_Borrar_Campo = new javax.swing.JMenuItem();
        jmi_Listar_Campos = new javax.swing.JMenuItem();
        jm_Registros = new javax.swing.JMenu();
        jmi_Crear_Registro = new javax.swing.JMenuItem();
        jmi_Borrar_Registro = new javax.swing.JMenuItem();
        jmi_Buscar_Registro = new javax.swing.JMenuItem();
        jmi_modreg = new javax.swing.JMenuItem();
        jmi_cruzar = new javax.swing.JMenuItem();
        jm_indices = new javax.swing.JMenu();
        jmi_crearindices = new javax.swing.JMenuItem();
        jmi_reindexar = new javax.swing.JMenuItem();
        jm_Estandarizacion = new javax.swing.JMenu();
        jmi_Exportar_Excel = new javax.swing.JMenuItem();
        jmi_Exportrar_XML = new javax.swing.JMenuItem();

        cbocampos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbocamposItemStateChanged(evt);
            }
        });

        jLabel1.setText("Seleccione el campo");

        jLabel2.setText("Nombre");

        jLabel3.setText("Tipo de dato");

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnSalir.setText("Salir");

        cbonuevo_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Int", "long", "String", "Char" }));

        javax.swing.GroupLayout JDMODIFICAR_CAMPOSLayout = new javax.swing.GroupLayout(JDMODIFICAR_CAMPOS.getContentPane());
        JDMODIFICAR_CAMPOS.getContentPane().setLayout(JDMODIFICAR_CAMPOSLayout);
        JDMODIFICAR_CAMPOSLayout.setHorizontalGroup(
            JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbocampos, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(156, 156, 156))))
                    .addGroup(JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(78, 78, 78)
                        .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtnuevo_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(cbonuevo_tipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        JDMODIFICAR_CAMPOSLayout.setVerticalGroup(
            JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDMODIFICAR_CAMPOSLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbocampos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtnuevo_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(82, 82, 82)
                .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbonuevo_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addGroup(JDMODIFICAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(106, 106, 106))
        );

        Table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        Table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table1MouseClicked(evt);
            }
        });
        Table1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Table1PropertyChange(evt);
            }
        });
        Listar_Campos.setViewportView(Table1);

        javax.swing.GroupLayout Listado_de_CamposLayout = new javax.swing.GroupLayout(Listado_de_Campos.getContentPane());
        Listado_de_Campos.getContentPane().setLayout(Listado_de_CamposLayout);
        Listado_de_CamposLayout.setHorizontalGroup(
            Listado_de_CamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Listado_de_CamposLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(Listar_Campos, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addGap(117, 117, 117))
        );
        Listado_de_CamposLayout.setVerticalGroup(
            Listado_de_CamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Listado_de_CamposLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Listar_Campos, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnCrear.setText("Crear");
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

        btnMostrar.setText("Mostrar");
        btnMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarActionPerformed(evt);
            }
        });

        jLabel4.setText("Nombre");

        jLabel5.setText("Tipo");

        txtcr_nombre.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        cbocr_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Int", "long", "String", "Char" }));

        javax.swing.GroupLayout JDCREAR_CAMPOLayout = new javax.swing.GroupLayout(JDCREAR_CAMPO.getContentPane());
        JDCREAR_CAMPO.getContentPane().setLayout(JDCREAR_CAMPOLayout);
        JDCREAR_CAMPOLayout.setHorizontalGroup(
            JDCREAR_CAMPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDCREAR_CAMPOLayout.createSequentialGroup()
                .addGroup(JDCREAR_CAMPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDCREAR_CAMPOLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(btnMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JDCREAR_CAMPOLayout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addGroup(JDCREAR_CAMPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))))
                .addContainerGap(102, Short.MAX_VALUE))
            .addComponent(txtcr_nombre)
            .addComponent(cbocr_tipo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        JDCREAR_CAMPOLayout.setVerticalGroup(
            JDCREAR_CAMPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDCREAR_CAMPOLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jLabel4)
                .addGap(35, 35, 35)
                .addComponent(txtcr_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jLabel5)
                .addGap(41, 41, 41)
                .addComponent(cbocr_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addGroup(JDCREAR_CAMPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );

        jLabel6.setText("Seleccione el campo que desea Eliminar: ");

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDELIMINAR_CAMPOSLayout = new javax.swing.GroupLayout(JDELIMINAR_CAMPOS.getContentPane());
        JDELIMINAR_CAMPOS.getContentPane().setLayout(JDELIMINAR_CAMPOSLayout);
        JDELIMINAR_CAMPOSLayout.setHorizontalGroup(
            JDELIMINAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cboEliminar, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(JDELIMINAR_CAMPOSLayout.createSequentialGroup()
                .addGroup(JDELIMINAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDELIMINAR_CAMPOSLayout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JDELIMINAR_CAMPOSLayout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jLabel6)))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        JDELIMINAR_CAMPOSLayout.setVerticalGroup(
            JDELIMINAR_CAMPOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDELIMINAR_CAMPOSLayout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jLabel6)
                .addGap(61, 61, 61)
                .addComponent(cboEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableMouseClicked(evt);
            }
        });
        Table.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                TablePropertyChange(evt);
            }
        });
        jsp_Tabla.setViewportView(Table);

        jm_Archivo.setText("Archivo");

        jmi_Nuevo_Archivo.setText("Nuevo Archivo");
        jmi_Nuevo_Archivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Nuevo_ArchivoActionPerformed(evt);
            }
        });
        jm_Archivo.add(jmi_Nuevo_Archivo);

        jmi_Salvar_Archivo.setText("Salvar Archivo");
        jmi_Salvar_Archivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Salvar_ArchivoActionPerformed(evt);
            }
        });
        jm_Archivo.add(jmi_Salvar_Archivo);

        jmi_Cerrar_Archivo.setText("Cerrar Archivo");
        jmi_Cerrar_Archivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Cerrar_ArchivoActionPerformed(evt);
            }
        });
        jm_Archivo.add(jmi_Cerrar_Archivo);

        jmi_Cargar_Archivo.setText("Cargar Archivo");
        jmi_Cargar_Archivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Cargar_ArchivoActionPerformed(evt);
            }
        });
        jm_Archivo.add(jmi_Cargar_Archivo);

        jmi_Salir.setText("Salir");
        jmi_Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_SalirActionPerformed(evt);
            }
        });
        jm_Archivo.add(jmi_Salir);

        jmb_Principal.add(jm_Archivo);

        jmi_Campos.setText("Campos");

        jmi_Crear_Campo.setText("Crear Campo");
        jmi_Crear_Campo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Crear_CampoActionPerformed(evt);
            }
        });
        jmi_Campos.add(jmi_Crear_Campo);

        jmi_Modificar_Campo.setText("Modificar Campo");
        jmi_Modificar_Campo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Modificar_CampoActionPerformed(evt);
            }
        });
        jmi_Campos.add(jmi_Modificar_Campo);

        jmi_Borrar_Campo.setText("Borrar Campo");
        jmi_Borrar_Campo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Borrar_CampoActionPerformed(evt);
            }
        });
        jmi_Campos.add(jmi_Borrar_Campo);

        jmi_Listar_Campos.setText("Listar Campos");
        jmi_Listar_Campos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Listar_CamposActionPerformed(evt);
            }
        });
        jmi_Campos.add(jmi_Listar_Campos);

        jmb_Principal.add(jmi_Campos);

        jm_Registros.setText("Registros");

        jmi_Crear_Registro.setText("Crear Registro");
        jmi_Crear_Registro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Crear_RegistroActionPerformed(evt);
            }
        });
        jm_Registros.add(jmi_Crear_Registro);

        jmi_Borrar_Registro.setText("Borrar Registro");
        jmi_Borrar_Registro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Borrar_RegistroActionPerformed(evt);
            }
        });
        jm_Registros.add(jmi_Borrar_Registro);

        jmi_Buscar_Registro.setText("Buscar Registro");
        jmi_Buscar_Registro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Buscar_RegistroActionPerformed(evt);
            }
        });
        jm_Registros.add(jmi_Buscar_Registro);

        jmi_modreg.setText("Modificar Registro");
        jmi_modreg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_modregActionPerformed(evt);
            }
        });
        jm_Registros.add(jmi_modreg);

        jmi_cruzar.setText("Cruzar Archivos");
        jmi_cruzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_cruzarActionPerformed(evt);
            }
        });
        jm_Registros.add(jmi_cruzar);

        jmb_Principal.add(jm_Registros);

        jm_indices.setText("Indice");

        jmi_crearindices.setText("Crear Indices");
        jmi_crearindices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_crearindicesActionPerformed(evt);
            }
        });
        jm_indices.add(jmi_crearindices);

        jmi_reindexar.setText("Re Indexar Archivos");
        jmi_reindexar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_reindexarActionPerformed(evt);
            }
        });
        jm_indices.add(jmi_reindexar);

        jmb_Principal.add(jm_indices);

        jm_Estandarizacion.setText("Estandarizacion");

        jmi_Exportar_Excel.setText("Exporat EXCEL");
        jmi_Exportar_Excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Exportar_ExcelActionPerformed(evt);
            }
        });
        jm_Estandarizacion.add(jmi_Exportar_Excel);

        jmi_Exportrar_XML.setText("Exportar XML");
        jmi_Exportrar_XML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi_Exportrar_XMLActionPerformed(evt);
            }
        });
        jm_Estandarizacion.add(jmi_Exportrar_XML);

        jmb_Principal.add(jm_Estandarizacion);

        setJMenuBar(jmb_Principal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jsp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jsp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmi_Nuevo_ArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Nuevo_ArchivoActionPerformed
        // TODO add your handling code here:
        Nuevo_Archivo();
    }//GEN-LAST:event_jmi_Nuevo_ArchivoActionPerformed

    private void jmi_Salvar_ArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Salvar_ArchivoActionPerformed
        // TODO add your handling code here:
        Salvar_Archivo();
    }//GEN-LAST:event_jmi_Salvar_ArchivoActionPerformed

    private void jmi_Cerrar_ArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Cerrar_ArchivoActionPerformed
        // TODO add your handling code here:
        try {
            RAfile.close();
            Table.setModel(cleanTable);
            JOptionPane.showMessageDialog(null, "Cerrado Exitosamente", "Cerrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jmi_Cerrar_ArchivoActionPerformed

    private void jmi_Cargar_ArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Cargar_ArchivoActionPerformed
        // TODO add your handling code here:
        Cargar_Archivo();
        if (FileSuccess == 1) {
            metadata = new Metadata();
            BuildTable(metadata, 1);
            try {
                CargarMetadatos();
                BuildTable(metadata, 0);
                LeerDatosRegistro();
                for (int i = 0; i < metadata.getCampos().size(); i++) {
                    Campo c = new Campo(metadata.getCampos().get(i).toString(), metadata.getTipos().get(i).toString());
                    listcampos.add(c);
                }
                contador = listcampos.size();
            } catch (ClassNotFoundException ex) {
            }
        }
    }//GEN-LAST:event_jmi_Cargar_ArchivoActionPerformed

    private void jmi_SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_SalirActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jmi_SalirActionPerformed

    private void jmi_Crear_CampoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Crear_CampoActionPerformed
        // TODO add your handling code here:
        JDCREAR_CAMPO.setModal(true);
        JDCREAR_CAMPO.pack();
        JDCREAR_CAMPO.setLocationRelativeTo(null);
        JDCREAR_CAMPO.setVisible(true);
    }//GEN-LAST:event_jmi_Crear_CampoActionPerformed

    private void jmi_Modificar_CampoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Modificar_CampoActionPerformed
        // TODO add your handling code here:
        JDMODIFICAR_CAMPOS.setModal(true);
        JDMODIFICAR_CAMPOS.pack();
        JDMODIFICAR_CAMPOS.setLocationRelativeTo(null);
        JDMODIFICAR_CAMPOS.setVisible(true);
        for (int i = 0; i < listcampos.size(); i++) {
            DefaultComboBoxModel modelo1;
            modelo1 = new DefaultComboBoxModel(listcampos.toArray());
            cbocampos.setModel(modelo1);
        }
    }//GEN-LAST:event_jmi_Modificar_CampoActionPerformed

    private void jmi_Borrar_CampoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Borrar_CampoActionPerformed
        // TODO add your handling code here:
        JDELIMINAR_CAMPOS.setModal(true);
        JDELIMINAR_CAMPOS.pack();
        JDELIMINAR_CAMPOS.setLocationRelativeTo(null);
        JDELIMINAR_CAMPOS.setVisible(true);
        for (int i = 0; i < listcampos.size(); i++) {
            DefaultComboBoxModel modelo1;
            modelo1 = new DefaultComboBoxModel(listcampos.toArray());
            cboEliminar.setModel(modelo1);
        }
    }//GEN-LAST:event_jmi_Borrar_CampoActionPerformed

    private void jmi_Listar_CamposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Listar_CamposActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "El primer campo es campo llave");
        Listado_de_Campos.setVisible(true);
        Listado_de_Campos.setLocationRelativeTo(this);
        Listado_de_Campos.setSize(500, 500);
        Listado_de_Campos.setTitle("Listado de Campos");
        Table1.setForeground(Color.BLACK);
        Table1.setBackground(Color.WHITE);
        Table1.setFont(new Font("", 1, 22));
        Table1.setRowHeight(30);
        Table1.putClientProperty("terminateEditOnFocusLost", true);
        String[] cols = {"", ""};
        DefaultTableModel tabla = new DefaultTableModel();
        tabla.addColumn("Campo");
        tabla.addColumn("Tipo");
        String tipo;
        for (int i = 0; i < metadata.getCampos().size(); i++) {
            tabla.addRow(cols);
        }

        Table1.setModel(tabla);
        int primero = 0;
        int segundo = 0;

        for (int i = 0; i < metadata.getCampos().size(); i++) {
            if (metadata.getTipos().get(i).toString().equals("Int")) {
                tabla.setValueAt(metadata.getCampos().get(i).toString(), primero, segundo);
                tabla.setValueAt("Entero", primero, segundo + 1);
                Table1.setModel(tabla);
            } else if (metadata.getTipos().get(i).toString().equals("long")) {
                tabla.setValueAt(metadata.getCampos().get(i), primero, segundo);
                tabla.setValueAt("Long", primero, segundo + 1);
                Table1.setModel(tabla);
            } else if (metadata.getTipos().get(i).toString().equals("String")) {
                tabla.setValueAt(metadata.getCampos().get(i), primero, segundo);
                tabla.setValueAt("String", primero, segundo + 1);
                Table1.setModel(tabla);
            } else if (metadata.getTipos().get(i).toString().equals("Char")) {
                tabla.setValueAt(metadata.getCampos().get(i), primero, segundo);
                tabla.setValueAt("Char", primero, segundo + 1);
                Table1.setModel(tabla);
            }
            primero++;
        }

    }//GEN-LAST:event_jmi_Listar_CamposActionPerformed

    private void jmi_Crear_RegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Crear_RegistroActionPerformed
        // TODO add your handling code here:
        if (metadata != null) {
            if (metadata.getCampos() != null) {
                if (metadata.getCampos().size() > 0) {
                    if (file == null) {
                        while (FileSuccess == 0) {
                            Crear_Archivo();

                        }

                        try {
                            Escribir_Metadatos();
                        } catch (IOException ex) {
                        }
                        Crear_Registro();
                    } else {
                        if (metadata.getNumregistros() < 1) {
                            try {
                                file.delete();
                                file.createNewFile();
                            } catch (Exception sdj) {
                            }

                            try {
                                Escribir_Metadatos();
                            } catch (IOException ex) {
                                //ex.printStackTrace();
                            }
                            metadata.addnumregistros();
                            Crear_Registro();
                        } else {
                            metadata.addnumregistros();
                            Crear_Registro();
                        }

                    }

                } else {
                    JOptionPane.showMessageDialog(null, "No hay campos creados! XTT 428");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No hay campos creados! XTT 431");
            }

        } else {
            JOptionPane.showMessageDialog(null, "No hay campos creados! XTT 435");
        }
    
    }//GEN-LAST:event_jmi_Crear_RegistroActionPerformed

    private void jmi_Borrar_RegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Borrar_RegistroActionPerformed
        // TODO add your handling code here:
        if (mode == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Registro para borrar.");
        } else {
            try {
                ArrayList export = new ArrayList();
                for (int i = 0; i < metadata.getCampos().size(); i++) {
                    export.add(Table.getValueAt(rowRemoval, i));
                }
                mode = -1;
                
                Eliminar_Dato_Archivo(export);
                metadata.subtractnumregistros();
                
                TableModel modelo = Table.getModel();
                DefaultTableModel model = (DefaultTableModel) modelo;
                model.removeRow(rowRemoval);
                Table.setModel(modelo);
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_jmi_Borrar_RegistroActionPerformed

    private void jmi_Buscar_RegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Buscar_RegistroActionPerformed
        // TODO add your handling code here:

        try {
            int Primarykey = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el PrimaryKey del registro a buscar."));
            Registro temporal = new Registro(Primarykey);
            BNode x;
            if ((x = metadata.getArbolB().search(temporal)) == null) {
                JOptionPane.showMessageDialog(null, "No se pudo encontrar");
            } else {

                Data datos = Buscar_Dato_Archivo(temporal);
                String info = "Registro: ";
                for (int i = 0; i < datos.datos.size(); i++) {
                    info += datos.datos.get(i) + " - ";
                }
                JOptionPane.showMessageDialog(this, info);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Operation aborted.");
        }
    }//GEN-LAST:event_jmi_Buscar_RegistroActionPerformed

    private void jmi_modregActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_modregActionPerformed
        // TODO add your handling code here:
 ArrayList export = new ArrayList();
        //pedir cual modificar
        //agregar a un temporal
        //eliminarlo
        if (mode == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Registro para modificar.");
        } else {
            try {
                
                for (int i = 0; i < metadata.getCampos().size(); i++) {
                    export.add(Table.getValueAt(rowRemoval, i));
                }
                mode = -1;
                Registro temporal = new Registro(Integer.parseInt(export.get(0).toString()));
                Crear_Registro();
                Eliminar_Dato_Archivo(export);
                metadata.subtractnumregistros();
                
                TableModel modelo = Table.getModel();
                DefaultTableModel model = (DefaultTableModel) modelo;
                model.removeRow(rowRemoval);
                Table.setModel(modelo);
            } catch (Exception e) {
            }
        }
        //crear uno nuevo con los datos de temporal
        //guardar
    }//GEN-LAST:event_jmi_modregActionPerformed

    private void jmi_cruzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_cruzarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jmi_cruzarActionPerformed

    private void jmi_crearindicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_crearindicesActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jmi_crearindicesActionPerformed

    private void jmi_reindexarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_reindexarActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jmi_reindexarActionPerformed

    private void jmi_Exportar_ExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Exportar_ExcelActionPerformed
        // TODO add your handling code here:
        try {
            if (file == null || metadata == null || metadata.getCampos() == null || metadata.getNumregistros() == 0) {
                JOptionPane.showMessageDialog(null, "No hay informacion cargada");
            } else {
                String name = JOptionPane.showInputDialog(null, "Ingrese el nombre del exporte: ");
                metodos.ExportToExcel(metadata, name, Table);
            }

        } catch (Exception e) {
        }

    }//GEN-LAST:event_jmi_Exportar_ExcelActionPerformed

    private void jmi_Exportrar_XMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi_Exportrar_XMLActionPerformed
        // TODO add your handling code here:
        try {
            if (file == null || metadata == null || metadata.getCampos() == null || metadata.getNumregistros() == 0) {
                JOptionPane.showMessageDialog(null, "No hay informacion cargada");
            } else {
                String name = JOptionPane.showInputDialog(null, "Ingrese el nombre del exporte: ");
                ArrayList registrost = new ArrayList();

                for (int i = 0; i < Table.getRowCount(); i++) {
                    ArrayList row = new ArrayList();
                    for (int j = 0; j < Table.getColumnCount(); j++) {
                        row.add(Table.getValueAt(i, j));
                    }
                    registrost.add(row);
                }
                exportXML(metadata.getCampos(), registrost, name);
            }

        } catch (Exception e) {
            System.out.println("Could not export successfully");
        }
    }//GEN-LAST:event_jmi_Exportrar_XMLActionPerformed

    private void TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseClicked
        // TODO add your handling code here:
         rowRemoval = Table.getSelectedRow();
        mode = 0;
    }//GEN-LAST:event_TableMouseClicked

    private void TablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_TablePropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_TablePropertyChange

    private void Table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Table1MouseClicked

    private void Table1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Table1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_Table1PropertyChange

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
        try {
            String nombre = "", tipo = "";
            nombre = txtcr_nombre.getText();
            tipo = cbocr_tipo.getSelectedItem().toString();
            Campo c = new Campo(nombre, tipo);
            listcampos.add(c);
            metodos.CreateCampos(metadata, nombre, tipo, contador);
            contador++;
        } catch (IOException ex) {
        } catch (ParseException ex) {
        }
        txtcr_nombre.setText("");
        cbocr_tipo.setSelectedIndex(0);
    }//GEN-LAST:event_btnCrearActionPerformed

    private void btnMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarActionPerformed
        // TODO add your handling code here:
        BuildTable(metadata, 0);
    }//GEN-LAST:event_btnMostrarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        int posicion = 0;
        if (metadata.getNumregistros() == 0 && metadata.getCampos() != null) {
            try {
                posicion = cboEliminar.getSelectedIndex();
                if (metadata.getCampos().size() == 0) {
                    JOptionPane.showMessageDialog(null, "Operacion Invalida");
                } else {
                    metodos.DeleteCampos(metadata, posicion);
                    BuildTable(metadata, 0);
                    listcampos.remove(posicion);
                }
            } catch (Exception e) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operacion Invalida");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void cbocamposItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbocamposItemStateChanged
        // TODO add your handling code here:
        if (cbocampos.getSelectedIndex() >= 0) {
            Campo s = (Campo) cbocampos.getSelectedItem();
            txtnuevo_Nombre.setText(s.getNombre());
            String Tipo = s.getTipo();
            if (Tipo.equals("Int")) {
                cbonuevo_tipo.setSelectedIndex(1);
            } else if (Tipo.equals("long")) {
                cbonuevo_tipo.setSelectedIndex(2);
            } else if (Tipo.equals("String")) {
                cbonuevo_tipo.setSelectedIndex(3);
            } else if (Tipo.equals("Char")) {
                cbonuevo_tipo.setSelectedIndex(4);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un campo");
        }
    }//GEN-LAST:event_cbocamposItemStateChanged

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        String nuevo_nombre = "", nuevo_tipo = "";
        int posicion = 0;
        nuevo_nombre = txtnuevo_Nombre.getText();
        nuevo_tipo = cbonuevo_tipo.getSelectedItem().toString();
        posicion = cbocampos.getSelectedIndex();
        if (metadata.getNumregistros() == 0 && metadata.getCampos() != null) {
            try {
                if (metadata.getCampos().size() == 0) {

                } else {
                    metodos.ModificarCampos(metadata, nuevo_nombre, nuevo_tipo, posicion);
                    BuildTable(metadata, 0);
                    listcampos.get(posicion).setNombre(nuevo_nombre);
                    listcampos.get(posicion).setTipo(nuevo_tipo);
                }

            } catch (Exception e) {

            }

        } else {
            JOptionPane.showMessageDialog(null, "Invalid Operation");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog JDCREAR_CAMPO;
    private javax.swing.JDialog JDELIMINAR_CAMPOS;
    private javax.swing.JDialog JDMODIFICAR_CAMPOS;
    private javax.swing.JDialog Listado_de_Campos;
    private javax.swing.JScrollPane Listar_Campos;
    private javax.swing.JTable Table;
    private javax.swing.JTable Table1;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnMostrar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboEliminar;
    private javax.swing.JComboBox<String> cbocampos;
    private javax.swing.JComboBox<String> cbocr_tipo;
    private javax.swing.JComboBox<String> cbonuevo_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jm_Archivo;
    private javax.swing.JMenu jm_Estandarizacion;
    private javax.swing.JMenu jm_Registros;
    private javax.swing.JMenu jm_indices;
    private javax.swing.JMenuBar jmb_Principal;
    private javax.swing.JMenuItem jmi_Borrar_Campo;
    private javax.swing.JMenuItem jmi_Borrar_Registro;
    private javax.swing.JMenuItem jmi_Buscar_Registro;
    private javax.swing.JMenu jmi_Campos;
    private javax.swing.JMenuItem jmi_Cargar_Archivo;
    private javax.swing.JMenuItem jmi_Cerrar_Archivo;
    private javax.swing.JMenuItem jmi_Crear_Campo;
    private javax.swing.JMenuItem jmi_Crear_Registro;
    private javax.swing.JMenuItem jmi_Exportar_Excel;
    private javax.swing.JMenuItem jmi_Exportrar_XML;
    private javax.swing.JMenuItem jmi_Listar_Campos;
    private javax.swing.JMenuItem jmi_Modificar_Campo;
    private javax.swing.JMenuItem jmi_Nuevo_Archivo;
    private javax.swing.JMenuItem jmi_Salir;
    private javax.swing.JMenuItem jmi_Salvar_Archivo;
    private javax.swing.JMenuItem jmi_crearindices;
    private javax.swing.JMenuItem jmi_cruzar;
    private javax.swing.JMenuItem jmi_modreg;
    private javax.swing.JMenuItem jmi_reindexar;
    private javax.swing.JScrollPane jsp_Tabla;
    private javax.swing.JTextField txtcr_nombre;
    private javax.swing.JTextField txtnuevo_Nombre;
    // End of variables declaration//GEN-END:variables
    Metadata metadata;
    int FileSuccess;
    Metodos metodos = new Metodos();
    File file;
    RandomAccessFile RAfile;
    int mode = -1;
    int rowRemoval;
    TableModel cleanTable;
    LinkedList AvailList = new LinkedList();
    ArrayList<Object> Export2;
    int tablemodification;
    Object oldcellvalue;
    int currentRow;
    int currentColumn;
}
