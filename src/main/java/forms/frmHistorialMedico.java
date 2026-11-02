package forms;

import blockchain.Block;
import blockchain.BlockChain;
import blockchain.Medico;
import blockchain.Paciente;
import blockchain.RegistroMedico;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class frmHistorialMedico extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(frmHistorialMedico.class.getName());

    // PALETA CORPORATIVA
    private static final Color PRIMARIO       = new Color(0x17324D); // azul marino
    private static final Color ACENTO         = new Color(0x2F6F8F); // azul acero
    private static final Color ACENTO_HOVER   = new Color(0x265A75);
    private static final Color FONDO          = new Color(0xF4F6F8); // gris perla
    private static final Color SUPERFICIE     = Color.WHITE;
    private static final Color BORDE          = new Color(0xD9DFE5);
    private static final Color TABLA_CABECERA = new Color(0xE9EDF1);
    private static final Color FILA_ALT       = new Color(0xF8FAFB);
    private static final Color SELECCION      = new Color(0xDCE8F0);
    private static final Color TEXTO          = new Color(0x1F2933);
    private static final Color TEXTO_SUAVE    = new Color(0x5F6B76);
    private static final Color DESHAB_BG      = new Color(0xE4E7EB);
    private static final Color DESHAB_FG      = new Color(0x9AA5B1);
    private static final Color FICHA_BG       = new Color(0xEEF3F7);

    private static final Font FUENTE_CARD  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_TEXTO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_MONO  = new Font("Consolas", Font.PLAIN, 13);

    // Motor de blockchain (Primer Parcial)
    private BlockChain bc;
    private DefaultTableModel modeloTablaBloques;
    private DefaultTableModel modeloTablaHistorial;

    public frmHistorialMedico() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Historial Medico - BlockChain (Modulo Bloque / Minado)");
        getContentPane().setBackground(FONDO);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(FONDO);
        body.setBorder(new EmptyBorder(12, 16, 12, 16));
        body.add(buildTopPanel(), BorderLayout.NORTH);
        body.add(buildCenterPanel(), BorderLayout.CENTER);
        body.add(buildBottomPanel(), BorderLayout.SOUTH);
        getContentPane().add(body, BorderLayout.CENTER);

        habilitar(false, false, false);

        setPreferredSize(new Dimension(1500, 900));
        setMinimumSize(new Dimension(1150, 750));

        pack();
        setLocationRelativeTo(null);
    }

    // Header
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARIO);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel titulo = new JLabel("Historial Medico en BlockChain");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        lblEstado.setFont(FUENTE_TEXTO);
        lblEstado.setForeground(new Color(0xC9D6E2));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(lblEstado);

        header.add(titulo, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // Fila superior: 2 tarjetas
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 0));
        top.setBackground(FONDO);
        top.setPreferredSize(new Dimension(100, 92));

        top.add(cardStart());
        top.add(cardRoutines());

        btnStart.addActionListener(evt -> btnStartActionPerformed(evt));
        btnCrearBloque.addActionListener(evt -> btnCrearBloqueActionPerformed(evt));
        btnAgregarRegistro.addActionListener(evt -> abrirDialogoAgregarRegistro());
        btnMinar.addActionListener(evt -> btnMinarActionPerformed(evt));

        return top;
    }

    private JPanel cardStart() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(SUPERFICIE);
        GridBagConstraints c = baseConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        content.add(etiqueta("Complejidad:"), c);
        c.gridx = 1; spComplexity.setPreferredSize(new Dimension(70, 28)); content.add(spComplexity, c);
        c.gridx = 2; c.insets = new Insets(2, 16, 2, 0); content.add(etiqueta("Caracter:"), c);
        c.gridx = 3; c.insets = new Insets(2, 4, 2, 0); cbMineChar.setPreferredSize(new Dimension(60, 28)); content.add(cbMineChar, c);
        c.gridx = 4; c.insets = new Insets(2, 16, 2, 0); c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        content.add(btnStart, c);

        return tarjeta("1. Iniciar BlockChain", content);
    }

    private JPanel cardRoutines() {
        JPanel content = new JPanel(new GridLayout(1, 3, 10, 0));
        content.setBackground(SUPERFICIE);
        content.add(btnCrearBloque);
        content.add(btnAgregarRegistro);
        content.add(btnMinar);

        return tarjeta("2. Rutinas del bloque", content);
    }

    // Centro: bitacora + tabla de bloques
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.setBackground(FONDO);

        center.add(panelConsola("Bitacora de la cadena", txtLog));
        center.add(panelTablaBloques());

        return center;
    }

    /** Titulo de panel minimalista: fondo blanco, texto marino y linea inferior. */
    private JLabel tituloPanel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setOpaque(true);
        lbl.setBackground(SUPERFICIE);
        lbl.setForeground(PRIMARIO);
        lbl.setFont(FUENTE_CARD);
        lbl.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, BORDE),
                new EmptyBorder(10, 12, 8, 12)));
        return lbl;
    }

    private JPanel panelConsola(String titulo, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SUPERFICIE);
        p.setBorder(new LineBorder(BORDE, 1, true));

        area.setEditable(false);
        area.setFont(FUENTE_MONO);
        area.setForeground(TEXTO);
        area.setBackground(SUPERFICIE);
        area.setMargin(new Insets(10, 10, 10, 10));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(null);
        sp.getViewport().setBackground(SUPERFICIE);

        p.add(tituloPanel(titulo), BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelTablaBloques() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SUPERFICIE);
        p.setBorder(new LineBorder(BORDE, 1, true));

        modeloTablaBloques = new DefaultTableModel(
                new Object[]{"ID", "Hash", "Hash anterior", "Nonce", "Fecha", "# Registros"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBloques.setModel(modeloTablaBloques);
        estilizarTabla(tblBloques, 26);

        JScrollPane sp = new JScrollPane(tblBloques);
        sp.setBorder(null);
        sp.getViewport().setBackground(SUPERFICIE);

        p.add(tituloPanel("Visor de bloques minados"), BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // Fila inferior: historial del paciente
    // Se separa en 2 partes: una "ficha" fija con los datos del paciente (que no
    // cambian entre visitas) y una tabla angosta con solo las visitas medicas
    // (esa si cambia por cada registro). Asi la tabla no necesita scroll horizontal.

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(SUPERFICIE);
        bottom.setBorder(new LineBorder(BORDE, 1, true));
        bottom.setPreferredSize(new Dimension(100, 320));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        barra.setBackground(SUPERFICIE);
        JLabel lbl = new JLabel("Ver historial completo del paciente (DUI):");
        lbl.setFont(FUENTE_CARD);
        lbl.setForeground(PRIMARIO);
        barra.add(lbl);
        txtConsultaDui.setPreferredSize(new Dimension(120, 26));
        txtConsultaDui.setToolTipText("Formato: NNNNNNNN-N");
        barra.add(txtConsultaDui);
        JButton btnBuscar = crearBoton("Buscar", ACENTO);
        barra.add(btnBuscar);
        btnBuscar.addActionListener(evt -> btnConsultarActionPerformed(evt));
        JButton btnLimpiar = crearBotonSecundario("Limpiar");
        barra.add(btnLimpiar);
        btnLimpiar.addActionListener(evt -> btnLimpiarActionPerformed(evt));
        btnActualizar.setToolTipText("Busca un DUI existente y abre su ficha para actualizar contacto/consulta");
        barra.add(btnActualizar);
        btnActualizar.addActionListener(evt -> btnActualizarActionPerformed(evt));

        // El boton solo se habilita tras una busqueda con resultados.
        btnActualizar.setEnabled(false);
        txtConsultaDui.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void invalidar() { btnActualizar.setEnabled(false); }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { invalidar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { invalidar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { invalidar(); }
        });

        // Ficha del paciente: fondo azul palido con linea izquierda de acento
        panelFicha.setLayout(new GridLayout(2, 1));
        panelFicha.setBackground(FICHA_BG);
        panelFicha.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDE),
                new CompoundBorder(
                        new MatteBorder(0, 3, 0, 0, ACENTO),
                        new EmptyBorder(8, 13, 8, 16))));
        lblFichaPaciente.setFont(FUENTE_CARD);
        lblFichaPaciente.setForeground(TEXTO);
        lblFichaResponsable.setFont(FUENTE_TEXTO);
        lblFichaResponsable.setForeground(TEXTO_SUAVE);
        panelFicha.add(lblFichaPaciente);
        panelFicha.add(lblFichaResponsable);
        panelFicha.setVisible(false); // se muestra solo cuando hay resultados

        // Tabla de VISITAS (solo lo que cambia por registro). Pocas columnas -> sin scroll horizontal.
        modeloTablaHistorial = new DefaultTableModel(
                new Object[]{"Fecha", "Tipo de registro", "Medico", "Especialidad", "Descripcion / Diagnostico"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblHistorial.setModel(modeloTablaHistorial);
        estilizarTabla(tblHistorial, 28);
        tblHistorial.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        int[] anchos = {130, 170, 140, 140};
        for (int i = 0; i < anchos.length; i++) {
            tblHistorial.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(SUPERFICIE);
        norte.add(barra, BorderLayout.NORTH);
        norte.add(panelFicha, BorderLayout.SOUTH);

        bottom.add(norte, BorderLayout.NORTH);
        JScrollPane scrollTabla = new JScrollPane(tblHistorial,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTabla.setBorder(new MatteBorder(1, 0, 0, 0, BORDE));
        scrollTabla.getViewport().setBackground(SUPERFICIE);
        bottom.add(scrollTabla, BorderLayout.CENTER);
        return bottom;
    }

    // Dialogo modal: Agregar / Actualizar registro medico
    private void abrirDialogoAgregarRegistro() {
        abrirDialogoRegistro(null, false);
    }

    /**
     * Dialogo de registro. Si pPrecarga es null -> modo agregar (todo editable).
     * Si pPrecarga trae una ficha -> modo actualizar: precarga la ficha descifrada
     * y bloquea nombres, apellidos y DUI; fecha de nacimiento, tipo de sangre,
     * el contacto del responsable y la consulta quedan editables.
     */
    private boolean abrirDialogoRegistro(Paciente pPrecarga, boolean modoActualizacion) {
        final Paciente precarga = modoActualizacion ? pPrecarga : null;
        final boolean[] guardado = { false };
        JDialog dlg = new JDialog(this,
                modoActualizacion ? "Actualizar registro medico" : "Agregar registro medico", true);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(FONDO);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARIO);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));
        JLabel titulo = new JLabel(modoActualizacion ? "Actualizar registro medico" : "Nuevo registro medico");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        header.add(titulo, BorderLayout.WEST);
        dlg.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FUENTE_CARD);

        // Pestana 1: datos del paciente (2 columnas)
        JTextField dNombres = new JTextField();
        JTextField dApellidos = new JTextField();
        JTextField dDui = new JTextField();
        dDui.setToolTipText("Formato: NNNNNNNN-N (ej. 04521678-9)");

        // Selector de fecha: spinner de fecha que NO permite elegir un dia futuro.
        SpinnerDateModel modeloFecha = new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH);
        JSpinner dFechaNac = new JSpinner(modeloFecha);
        dFechaNac.setEditor(new JSpinner.DateEditor(dFechaNac, "dd/MM/yyyy"));
        dFechaNac.setToolTipText("No se permiten fechas futuras a hoy");
        // El limite "max" del modelo solo aplica a las flechas del spinner; si el usuario
        // escribe la fecha a mano puede saltarselo, asi que tambien se corrige aqui al vuelo.
        dFechaNac.addChangeListener(e -> {
            Date valor = (Date) dFechaNac.getValue();
            if (valor.after(new Date())) {
                dFechaNac.setValue(new Date());
            }
        });

        JComboBox<String> dTipoSangre = new JComboBox<>(new String[]{
            "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-",
            "Pendiente de examen", "Desconocido / No especifica"
        });

        JTextField dFamiliar = new JTextField();
        JTextField dDuiResp = new JTextField();
        dDuiResp.setToolTipText("Formato: NNNNNNNN-N");
        JComboBox<String> dParentesco = new JComboBox<>(
                new String[]{"Padre", "Madre", "Conyuge", "Hijo/a", "Hermano/a", "Otro"});
        JTextField dTelefonoResp = new JTextField();
        dTelefonoResp.setToolTipText("Formato: NNNN-NNNN");
        JTextField dDireccionResp = new JTextField();

        // Modo actualizar: precargar la ficha descifrada y bloquear identidad
        if (modoActualizacion && precarga != null) {
            dNombres.setText(precarga.getNombres());
            dApellidos.setText(precarga.getApellidos());
            dDui.setText(precarga.getDui());
            dTipoSangre.setSelectedItem(precarga.getTipoSangre());

            try {
                dFechaNac.setValue(new SimpleDateFormat("dd/MM/yyyy").parse(precarga.getFechaNacimiento()));
            } catch (Exception ignored) { /* si la fecha no parsea, se deja la actual */ }

            dFamiliar.setText(precarga.getFamiliarResponsable());
            dDuiResp.setText(precarga.getDuiResponsable());
            dParentesco.setSelectedItem(precarga.getParentesco());
            dTelefonoResp.setText(precarga.getTelefonoResponsable());
            dDireccionResp.setText(precarga.getDireccionResponsable());

            // Identidad fija: nombres, apellidos y DUI no editables.
            // Fecha de nacimiento y tipo de sangre SI se pueden corregir/actualizar.
            String tipBloqueado = "Nombre y DUI: no editables";
            dNombres.setEditable(false); dNombres.setBackground(DESHAB_BG); dNombres.setToolTipText(tipBloqueado);
            dApellidos.setEditable(false); dApellidos.setBackground(DESHAB_BG); dApellidos.setToolTipText(tipBloqueado);
            dDui.setEditable(false); dDui.setBackground(DESHAB_BG); dDui.setToolTipText(tipBloqueado);
        }

        JPanel colIzq = new JPanel(new GridBagLayout());
        colIzq.setBackground(FONDO);
        GridBagConstraints ci = camposConstraints();
        int yi = 0;
        yi = addCampo(colIzq, ci, yi, "Nombres del paciente:", dNombres);
        yi = addCampo(colIzq, ci, yi, "Apellidos del paciente:", dApellidos);
        yi = addCampo(colIzq, ci, yi, "DUI del paciente:", dDui);
        yi = addCampo(colIzq, ci, yi, "Fecha de nacimiento:", dFechaNac);
        yi = addCampo(colIzq, ci, yi, "Tipo de sangre:", dTipoSangre);

        JPanel colDer = new JPanel(new GridBagLayout());
        colDer.setBackground(FONDO);
        GridBagConstraints cd = camposConstraints();
        int yd = 0;
        yd = addCampo(colDer, cd, yd, "Familiar responsable:", dFamiliar);
        yd = addCampo(colDer, cd, yd, "DUI del responsable:", dDuiResp);
        yd = addCampo(colDer, cd, yd, "Parentesco:", dParentesco);
        yd = addCampo(colDer, cd, yd, "Telefono del responsable:", dTelefonoResp);
        yd = addCampo(colDer, cd, yd, "Direccion del responsable:", dDireccionResp);

        JPanel tabPaciente = new JPanel(new GridLayout(1, 2, 20, 0));
        tabPaciente.setBackground(FONDO);
        tabPaciente.setBorder(new EmptyBorder(16, 20, 10, 20));
        tabPaciente.add(colIzq);
        tabPaciente.add(colDer);
        tabs.addTab("Datos del paciente", tabPaciente);

        // Pestana 2: consulta medica
        JComboBox<Medico> dMedico = new JComboBox<>(Medico.CATALOGO);
        JLabel lblEspecialidad = new JLabel();
        lblEspecialidad.setFont(FUENTE_TEXTO);
        lblEspecialidad.setForeground(TEXTO_SUAVE);
        JComboBox<String> dTipoRegistro = new JComboBox<>();
        JTextArea dDescripcion = new JTextArea();
        dDescripcion.setLineWrap(true);
        dDescripcion.setWrapStyleWord(true);
        dDescripcion.setFont(FUENTE_TEXTO);
        dDescripcion.setForeground(TEXTO);

        Runnable actualizarTipos = () -> {
            Medico m = (Medico) dMedico.getSelectedItem();
            dTipoRegistro.removeAllItems();
            if (m != null) {
                lblEspecialidad.setText("Especialidad: " + m.getEspecialidad() + "   |   JVPM: " + m.getJvpm());
                List<String> tipos = Medico.TIPOS_POR_ESPECIALIDAD.get(m.getEspecialidad());
                if (tipos != null) {
                    for (String t : tipos) dTipoRegistro.addItem(t);
                }
            }
        };
        dMedico.addActionListener(e -> actualizarTipos.run());

        // Fila superior: Medico + Especialidad (autocompletada) + Tipo de registro
        JPanel arriba = new JPanel(new GridBagLayout());
        arriba.setBackground(FONDO);
        GridBagConstraints cai = camposConstraints();
        int yai = 0;
        yai = addCampo(arriba, cai, yai, "Medico:", dMedico);
        cai.gridy = yai++; cai.insets = new Insets(2, 0, 10, 0); arriba.add(lblEspecialidad, cai);
        yai = addCampo(arriba, cai, yai, "Tipo de registro:", dTipoRegistro);

        JPanel tabConsulta = new JPanel(new BorderLayout(0, 6));
        tabConsulta.setBackground(FONDO);
        tabConsulta.setBorder(new EmptyBorder(16, 20, 10, 20));
        tabConsulta.add(arriba, BorderLayout.NORTH);

        JPanel panelDescripcion = new JPanel(new BorderLayout(0, 4));
        panelDescripcion.setBackground(FONDO);
        panelDescripcion.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelDescripcion.add(etiqueta("Descripcion / diagnostico clinico:"), BorderLayout.NORTH);
        JScrollPane spDesc = new JScrollPane(dDescripcion);
        spDesc.setBorder(new LineBorder(BORDE, 1));
        spDesc.setPreferredSize(new Dimension(100, 220));
        panelDescripcion.add(spDesc, BorderLayout.CENTER);
        tabConsulta.add(panelDescripcion, BorderLayout.CENTER);

        tabs.addTab("Consulta medica", tabConsulta);

        actualizarTipos.run(); // inicializa con el primer medico del catalogo

        dlg.add(tabs, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botones.setBackground(FONDO);
        botones.setBorder(new EmptyBorder(0, 24, 16, 24));
        JButton btnCancelar = crearBotonSecundario("Cancelar");
        JButton btnGuardar = crearBoton(modoActualizacion ? "Guardar actualizacion" : "Guardar registro", ACENTO);
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        dlg.add(botones, BorderLayout.SOUTH);

        SimpleDateFormat fmtFecha = new SimpleDateFormat("dd/MM/yyyy");

        btnCancelar.addActionListener(evt -> dlg.dispose());
        btnGuardar.addActionListener(evt -> {
            String fechaNacTexto = fmtFecha.format((Date) dFechaNac.getValue());
            boolean ok = guardarRegistro(
                    dNombres.getText(), dApellidos.getText(), dDui.getText(), fechaNacTexto,
                    dTipoSangre.getSelectedItem().toString(),
                    dFamiliar.getText(), dDuiResp.getText(), dParentesco.getSelectedItem().toString(),
                    dTelefonoResp.getText(), dDireccionResp.getText(), (Medico) dMedico.getSelectedItem(),
                    dTipoRegistro.getSelectedItem() == null ? "" : dTipoRegistro.getSelectedItem().toString(),
                    dDescripcion.getText(), modoActualizacion);
            if (ok) {
                guardado[0] = true;
                dlg.dispose();
            }
        });

        dlg.setSize(760, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        return guardado[0];
    }

    /** Agrega una fila "etiqueta + campo" a un formulario GridBag y devuelve el siguiente gridy. */
    private int addCampo(JPanel panel, GridBagConstraints c, int y, String etiqueta, JComponent campo) {
        c.gridy = y++; c.insets = new Insets(10, 0, 4, 0); panel.add(etiqueta(etiqueta), c);
        c.gridy = y++; c.insets = new Insets(2, 0, 4, 0); panel.add(campo, c);
        return y;
    }

    private GridBagConstraints camposConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        return c;
    }

    /** Devuelve true si se guardo correctamente (para cerrar el dialogo). */
    private boolean guardarRegistro(String nombresRaw, String apellidosRaw, String duiRaw, String fechaNac,
                                     String tipoSangre, String familiarRaw, String duiRespRaw, String parentesco,
                                     String telefonoRespRaw, String direccionRespRaw, Medico medico, String tipoRegistro,
                                     String descripcionRaw, boolean modoActualizacion) {

        String nombres = nombresRaw.trim();
        String apellidos = apellidosRaw.trim();
        String dui = duiRaw.trim();
        String familiar = familiarRaw.trim();
        String duiResp = duiRespRaw.trim();
        String telefonoResp = telefonoRespRaw.trim();
        String direccionResp = direccionRespRaw.trim();
        String descripcion = descripcionRaw.trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || dui.isEmpty() || descripcion.isEmpty() || medico == null) {
            JOptionPane.showMessageDialog(this,
                    "Completa al menos: nombres, apellidos, DUI del paciente, medico y descripcion.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!Paciente.esFormatoDuiValido(dui)) {
            JOptionPane.showMessageDialog(this,
                    "El DUI del paciente debe tener el formato NNNNNNNN-N (ej. 04521678-9).",
                    "Formato invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Verificacion de respaldo: el spinner de fecha limita las flechas, pero si el
        // usuario ESCRIBE la fecha a mano puede saltarse ese limite. Se valida aqui tambien.
        try {
            Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaNac);
            if (fecha.after(new Date())) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de nacimiento no puede ser una fecha futura.",
                        "Fecha invalida", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha de nacimiento invalida.",
                    "Fecha invalida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!duiResp.isEmpty() && !Paciente.esFormatoDuiValido(duiResp)) {
            JOptionPane.showMessageDialog(this,
                    "El DUI del responsable debe tener el formato NNNNNNNN-N.",
                    "Formato invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!telefonoResp.isEmpty() && !Paciente.esFormatoTelefonoValido(telefonoResp)) {
            JOptionPane.showMessageDialog(this,
                    "El telefono del responsable debe tener el formato NNNN-NNNN.",
                    "Formato invalido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Modo actualizar: verificar que la identidad no fue alterada (debe coincidir
        // con la ultima ficha registrada para ese DUI).
        if (modoActualizacion) {
            List<RegistroMedico> hist = bc.obtenerHistorial(dui);
            if (hist.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No existe un paciente con ese DUI. Use 2 Agregar registro para crearlo.",
                        "Paciente no encontrado", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            RegistroMedico ult = hist.get(hist.size() - 1);
            Paciente actual = ult.isEncriptado() ? ult.getPacienteDescifrado(bc) : ult.getPaciente();
            // Nombres, apellidos y DUI son fijos. Fecha de nacimiento y tipo de
            // sangre SI pueden actualizarse en esta version.
            boolean identidadIgual = actual.getNombres().equals(nombres)
                    && actual.getApellidos().equals(apellidos)
                    && actual.getDui().equals(dui);
            if (!identidadIgual) {
                JOptionPane.showMessageDialog(this,
                        "Los nombres, apellidos y DUI del paciente no se pueden modificar.",
                        "Identidad no editable", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        Paciente paciente = new Paciente(nombres, apellidos, dui, fechaNac, tipoSangre,
                familiar, duiResp, parentesco, telefonoResp, direccionResp);

        // Cifrado transparente: se cifra con la llave de sesion, sin pedir password.
        try
        {
            bc.getLastBlock().addRegistroCifrado(paciente, medico, tipoRegistro, descripcion, bc);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cifrar el registro: " + ex.getMessage(),
                    "Error de cifrado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int totalEnBloque = bc.getLastBlock().countRegistros();
        txtLog.append((modoActualizacion ? "\nRegistro actualizado [CIFRADO]" : "\nRegistro agregado [CIFRADO]")
                + " (# en bloque: " + totalEnBloque + ")");

        habilitar(false, true, true);
        return true;
    }

    // Helpers de estilo
    private JPanel tarjeta(String titulo, JPanel contenido) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(SUPERFICIE);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(BORDE, 1, true),
                new EmptyBorder(6, 14, 6, 14)));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(PRIMARIO);
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(contenido, BorderLayout.CENTER);

        return wrapper;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(2, 0, 2, 0);
        return c;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_TEXTO);
        l.setForeground(TEXTO);
        return l;
    }

    /** Tabla plana: fondo blanco, cabecera gris frio, filas alternas y seleccion azul palido. */
    private static void estilizarTabla(JTable tabla, int altoFila) {
        tabla.setFont(FUENTE_TEXTO);
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setRowHeight(altoFila);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foco, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setBackground(sel ? SELECCION : (row % 2 == 0 ? SUPERFICIE : FILA_ALT));
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        DefaultTableCellRenderer cab = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foco, int row, int col) {
                super.getTableCellRendererComponent(t, v, false, false, row, col);
                setOpaque(true);
                setBackground(TABLA_CABECERA);
                setForeground(PRIMARIO);
                setFont(FUENTE_CARD);
                setBorder(new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, BORDE),
                        new EmptyBorder(6, 8, 6, 8)));
                return this;
            }
        };
        cab.setHorizontalAlignment(SwingConstants.LEFT);
        tabla.getTableHeader().setDefaultRenderer(cab);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    /** Boton plano con esquinas redondeadas (ignora el degradado de Nimbus). */
    private static class BotonPlano extends JButton {
        private final Color base;
        private final boolean secundario;

        BotonPlano(String texto, Color base, boolean secundario) {
            super(texto);
            this.base = base;
            this.secundario = secundario;
            setFont(FUENTE_CARD);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 14, 10, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            Color fondo, texto, borde = null;
            if (!isEnabled()) {
                fondo = DESHAB_BG;
                texto = DESHAB_FG;
            } else if (secundario) {
                fondo = getModel().isRollover() || getModel().isPressed() ? FONDO : SUPERFICIE;
                texto = TEXTO_SUAVE;
                borde = BORDE;
            } else {
                fondo = getModel().isRollover() || getModel().isPressed() ? ACENTO_HOVER : base;
                texto = Color.WHITE;
            }

            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(texto);
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }

    private static JButton crearBoton(String texto, Color color) {
        BotonPlano b = new BotonPlano(texto, color, false);
        b.getModel().addChangeListener(e -> b.repaint());
        b.setRolloverEnabled(true);
        return b;
    }

    private static JButton crearBotonSecundario(String texto) {
        BotonPlano b = new BotonPlano(texto, SUPERFICIE, true);
        b.setRolloverEnabled(true);
        return b;
    }

    private void habilitar(boolean crear, boolean agregar, boolean minar) {
        btnCrearBloque.setEnabled(crear);
        btnAgregarRegistro.setEnabled(agregar);
        btnMinar.setEnabled(minar);
    }

    private void refrescarTablaBloques() {
        modeloTablaBloques.setRowCount(0);
        for (Object[] fila : bc.obtenerFilasTabla()) {
            modeloTablaBloques.addRow(fila);
        }
    }

    // Eventos: logica del Primer Parcial

    // Paso 1: crear y minar el bloque Genesis
    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {
        int complexity = Integer.parseInt(spComplexity.getValue().toString());
        String mineChar = cbMineChar.getSelectedItem().toString();
        this.bc = new BlockChain(complexity, mineChar);
        this.bc.createGenesis();
        Block blk = bc.getLastBlock();
        txtLog.setText("Bloque Genesis minado.\nHash: " + blk.getHash()
                + "\nTimestamp: " + blk.getTimeStamp()
                + "\n----------------------------------------------------------------");
        lblEstado.setText("Cadena activa  |  dificultad " + complexity + "  |  bloques: " + bc.size());
        refrescarTablaBloques();
        habilitar(true, false, false);
        btnActualizar.setEnabled(false);
    }

    // Paso 2: crear bloque propuesto
    private void btnCrearBloqueActionPerformed(java.awt.event.ActionEvent evt) {
        bc.createBlock();
        Block blk = bc.getLastBlock();
        txtLog.append("\nNuevo bloque propuesto. ID: " + blk.getId()
                + "\nPrevious Hash: " + blk.getPreviousHash());
        lblEstado.setText("Bloque #" + blk.getId() + " propuesto  |  bloques: " + bc.size());
        habilitar(false, true, false);
    }

    // Paso 4/5: minar (prueba de trabajo) y sellar el bloque en la cadena
    private void btnMinarActionPerformed(java.awt.event.ActionEvent evt) {
        long t0 = System.currentTimeMillis();
        bc.mineBlock();
        long ms = System.currentTimeMillis() - t0;
        Block blk = bc.getLastBlock();
        txtLog.append("\n--- MINADO (" + ms + " ms) ---\nNonce: " + blk.getNonce()
                + "\nHash:  " + blk.getHash()
                + "\n----------------------------------------------------------------");

        refrescarTablaBloques();
        lblEstado.setText("Cadena activa  |  bloques: " + bc.size());
        habilitar(true, false, false);
    }

    // Consulta de historial: llena la ficha del paciente y la tabla de visitas medicas
    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {
        modeloTablaHistorial.setRowCount(0);
        panelFicha.setVisible(false);
        btnActualizar.setEnabled(false);
        if (bc == null) return;

        String dui = txtConsultaDui.getText().trim();

        List<RegistroMedico> historial = bc.obtenerHistorial(dui);
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sin registros para ese DUI.");
            return;
        }

        // La ficha muestra los datos de la visita MAS RECIENTE (ultimo registro).
        // La tabla conserva todas las versiones en orden cronologico.
        // Descifrado transparente con la llave de sesion (registros viejos en claro se muestran directo).
        RegistroMedico ultimo = historial.get(historial.size() - 1);
        Paciente p = ultimo.isEncriptado() ? ultimo.getPacienteDescifrado(bc) : ultimo.getPaciente();
        lblFichaPaciente.setText("<html><b>Paciente:</b> " + p.getNombreCompleto()
                + " &nbsp;|&nbsp; <b>DUI:</b> " + p.getDui()
                + " &nbsp;|&nbsp; <b>F. Nacimiento:</b> " + p.getFechaNacimiento()
                + " &nbsp;|&nbsp; <b>Tipo de sangre:</b> " + p.getTipoSangre() + "</html>");
        lblFichaResponsable.setText("<html><b>Responsable:</b> " + p.getFamiliarResponsable()
                + " (" + p.getParentesco() + ") &nbsp;|&nbsp; <b>DUI:</b> " + p.getDuiResponsable()
                + " &nbsp;|&nbsp; <b>Tel:</b> " + p.getTelefonoResponsable()
                + " &nbsp;|&nbsp; <b>Direccion:</b> " + p.getDireccionResponsable() + "</html>");
        panelFicha.setVisible(true);

        for (RegistroMedico r : historial) {
            final Medico m;
            final String tipoShow;
            final String descShow;
            if (r.isEncriptado())
            {
                // Vista descifrada: se muestra el texto claro sin prefijos.
                m = r.getMedicoDescifrado(bc);
                tipoShow = r.getTipoRegistroDescifrado(bc);
                descShow = r.getDescripcionDescifrada(bc);
            }
            else
            {
                m = r.getMedico();
                tipoShow = r.getTipoRegistro();
                descShow = r.getDescripcion();
            }
            modeloTablaHistorial.addRow(new Object[]{
                fmt.format(new java.util.Date(r.getTimeStamp())),
                tipoShow, m.getNombre(), m.getEspecialidad(), descShow
            });
        }
        // Solo hay resultados en pantalla: se permite actualizar ese DUI.
        btnActualizar.setEnabled(true);
    }

    // Actualizar: solo para DUI existente. Precarga la ficha, bloquea identidad y
    // permite cambiar contacto del responsable + nueva consulta. Nunca crea pacientes.
    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {
        if (bc == null || bc.size() < 1) {
            JOptionPane.showMessageDialog(this,
                    "Primero inicie la cadena con Start / Create.",
                    "Cadena no iniciada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (bc.getLastBlock().getHash() != null) {
            JOptionPane.showMessageDialog(this,
                    "Primero pulse \"1 Crear bloque\" para tener un bloque pendiente donde guardar la actualizacion.",
                    "Sin bloque pendiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dui = txtConsultaDui.getText().trim();
        if (!Paciente.esFormatoDuiValido(dui)) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un DUI valido con formato NNNNNNNN-N (ej. 04521678-9).",
                    "Formato invalido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<RegistroMedico> historial = bc.obtenerHistorial(dui);
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No existe un paciente con ese DUI. Use \"2 Agregar registro\" para crearlo.",
                    "Paciente no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RegistroMedico ultimo = historial.get(historial.size() - 1);
        Paciente ficha = ultimo.isEncriptado() ? ultimo.getPacienteDescifrado(bc) : ultimo.getPaciente();
        boolean guardado = abrirDialogoRegistro(ficha, true);
        if (guardado) btnConsultarActionPerformed(evt);
    }

    // Limpia los campos de busqueda, la ficha del paciente y la tabla de historial
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        txtConsultaDui.setText("");
        modeloTablaHistorial.setRowCount(0);
        panelFicha.setVisible(false);
        btnActualizar.setEnabled(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Ajustes globales para que Nimbus no se vea gris oscuro
            UIManager.put("control", FONDO);
            UIManager.put("nimbusBlueGrey", new Color(0xD9DFE5));
            UIManager.put("nimbusSelectionBackground", ACENTO);
            UIManager.put("ScrollPane.background", SUPERFICIE);
            UIManager.put("Viewport.background", SUPERFICIE);
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new frmHistorialMedico().setVisible(true));
    }

    // Variables declaration
    private final JSpinner spComplexity = new JSpinner(new SpinnerNumberModel(4, 1, 6, 1));
    private final JComboBox<String> cbMineChar = new JComboBox<>(new String[]{"0", "1", "a"});
    private final JButton btnStart = crearBoton("Start / Create", ACENTO);
    private final JButton btnCrearBloque = crearBoton("1  Crear bloque", ACENTO);
    private final JButton btnAgregarRegistro = crearBoton("2  Agregar registro", ACENTO);
    private final JButton btnMinar = crearBoton("3  Minar bloque", ACENTO);
    private final JButton btnActualizar = crearBoton("Actualizar registro", ACENTO);

    private final JTextArea txtLog = new JTextArea();
    private final JTable tblBloques = new JTable();

    private final JTextField txtConsultaDui = new JTextField();
    private final JTable tblHistorial = new JTable();
    private final JPanel panelFicha = new JPanel();
    private final JLabel lblFichaPaciente = new JLabel();
    private final JLabel lblFichaResponsable = new JLabel();
    private final JLabel lblEstado = new JLabel("Cadena no iniciada");
}
