/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package minicasino_sql;

import static com.mysql.cj.telemetry.TelemetryAttribute.DB_USER;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

/**
 *
 * @author M0RD3K41
 */
public class Tragamonedas extends javax.swing.JFrame {

    /**
     * Creates new form Tragamonedas
     */
    
    //Declaracion de variables a usar
    private UserInformation activeUser;
    private JButton[][] carretesBotones = new JButton[3][3]; //Almacenar los botones del Tragamonedas en una Matriz
    private List<ImageIcon> iconos = new ArrayList<>(); //Guardar los iconos en una lista para poder intercambiarlos de manera aleatoria
    private Random random = new Random();
    private Timer giroTimer; // Un solo Timer para controlar el cambio continuo
    private Timer[] columnaTimers = new Timer[3];
    private Map<String, Integer> tablaPremios = new HashMap<>(); //Tabla de premios
    private List<ImageIcon> iconosPonderados = new ArrayList<>(); //Probabilidad de iconos
    private int lineasActivas = 1; // Siempre inicia con una línea activa
    private boolean[] columnasDetenidas = new boolean[3]; // Para rastrear qué columnas se han detenido
    private int apuestaActual = 50; // Apuesta inicial
    private final int APUESTA_MINIMA = 50;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/minicasino"; // Reemplazar con la URL de la base de datos
    private static final String DB_USER = "root"; // Reemplazar con el nombre de usuario de la base de datos
    private static final String DB_PASSWORD = "MiCasino_Equipo$01"; // Reemplazar con la contraseña de la base de datos
    
    
    //Todo aquellos que se inicializara al cambiar a la interfaz del Tragamonedas
    public Tragamonedas(UserInformation activeUser) {
        initComponents();
        setLocationRelativeTo(null);
        this.activeUser = activeUser;
        mostrarDatosUsuario();
        cargarIconos();
        inicializarProbabilidadesIconos();
        inicializarTablaPremios();
        asignarBotonesCarretes();
        inicializarGiroContinuo();
        agregarActionListenerJugar();
        JButton[] btns = {jButton19,jButton20,jButton21,jButton22,jButton23,jButton24,jButton25,jButton26,jButton27};
        for(JButton btn : btns){
            btn.setBackground(new Color(34,40,44));
            btn.setUI(new BasicButtonUI());
            btn.addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(MouseEvent e){
                }
                @Override
                public void mousePressed(MouseEvent e) { 
                }

                @Override
                public void mouseReleased(MouseEvent e) {                    
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(54,81,207));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(34,40,44));
                }
            });
        }
    }
    
    public Tragamonedas() {
        initComponents();
        setLocationRelativeTo(null);
        cargarIconos();
        inicializarProbabilidadesIconos();
        inicializarTablaPremios();
        asignarBotonesCarretes();
        inicializarGiroContinuo();
        agregarActionListenerJugar();
    }
    
    //Carga de iconos del Tragamonedas
     private void cargarIconos() {
        try {
            ImageIcon icono;
            icono = new ImageIcon(getClass().getResource("/resources/images/herradura.png"));
            icono.setDescription("herradura.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/cactus.png"));
            icono.setDescription("cactus.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/insignia-de-sheriff.png"));
            icono.setDescription("insignia-de-sheriff.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/lingotes-de-oro.png"));
            icono.setDescription("lingotes-de-oro.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/planta-rodadora.png"));
            icono.setDescription("planta-rodadora.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/revolver.png"));
            icono.setDescription("revolver.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/vaquero.png"));
            icono.setDescription("vaquero.png");
            iconos.add(icono);
            icono = new ImageIcon(getClass().getResource("/resources/images/caballo-levantando-pies-vista-lateral-silueta.png"));
            icono.setDescription("caballo-levantando-pies-vista-lateral-silueta.png");
            iconos.add(icono);
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
     //Asignando botones en un array para poder usarlos en diferentes funcioens
    private void asignarBotonesCarretes() {
        carretesBotones[0][0] = jButton1;
        carretesBotones[0][1] = jButton2;
        carretesBotones[0][2] = jButton3;
        carretesBotones[1][0] = jButton4;
        carretesBotones[1][1] = jButton7;
        carretesBotones[1][2] = jButton8;
        carretesBotones[2][0] = jButton9;
        carretesBotones[2][1] = jButton10;
        carretesBotones[2][2] = jButton11;
    }
    
    private void inicializarTablaPremios() {
        tablaPremios.put("herradura.png", 2);
        tablaPremios.put("cactus.png", 3);
        tablaPremios.put("insignia-de-sheriff.png", 5);
        tablaPremios.put("lingotes-de-oro.png", 10);
        tablaPremios.put("planta-rodadora.png", 2);
        tablaPremios.put("revolver.png", 4);
        tablaPremios.put("vaquero.png", 6);
        tablaPremios.put("caballo-levantando-pies-vista-lateral-silueta.png", 5);
    }
    
    private void actualizarLabelsPremios() {
        jLabel1.setText(String.valueOf(tablaPremios.get("herradura.png") * apuestaActual));
        jLabel5.setText(String.valueOf(tablaPremios.get("planta-rodadora.png") * apuestaActual));
        jLabel6.setText(String.valueOf(tablaPremios.get("cactus.png") * apuestaActual));
        jLabel7.setText(String.valueOf(tablaPremios.get("revolver.png") * apuestaActual));
        jLabel8.setText(String.valueOf(tablaPremios.get("insignia-de-sheriff.png") * apuestaActual));
        jLabel9.setText(String.valueOf(tablaPremios.get("caballo-levantando-pies-vista-lateral-silueta.png") * apuestaActual));
        jLabel10.setText(String.valueOf(tablaPremios.get("vaquero.png") * apuestaActual));
        jLabel11.setText(String.valueOf(tablaPremios.get("lingotes-de-oro.png") * apuestaActual));
        jLabel13.setText(String.valueOf(apuestaActual));
    }

    
    private void aumentarApuesta() {
        if (apuestaActual < activeUser.getBalance()) {
            apuestaActual += 50; // Incrementa la apuesta en 10
            actualizarLabelsPremios(); // Actualiza los labels de premios
        } else {
            JOptionPane.showMessageDialog(this, "No tienes suficientes créditos para aumentar la apuesta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disminuirApuesta() {
        if (apuestaActual > 50) { // La apuesta mínima es 10
            apuestaActual -= 50; // Decrementa la apuesta en 10
            actualizarLabelsPremios(); // Actualiza los labels de premios
        } else {
            JOptionPane.showMessageDialog(this, "La apuesta no puede ser menor a 50 créditos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inicializarProbabilidadesIconos() {
        for (ImageIcon icono : iconos) {
            String nombre = obtenerNombreArchivoDesdeIcono(icono);
            int cantidad = switch (nombre) {
                case "cactus.png" -> 8;
                case "planta-rodadora.png" -> 8;
                case "insignia-de-sheriff.png" -> 6;
                case "revolver.png" -> 5;
                case "vaquero.png" -> 4;
                case "caballo-levantando-pies-vista-lateral-silueta.png" -> 5;
                case "herradura.png" -> 7;
                case "lingotes-de-oro.png" -> 2;
                default -> 5;
            };
            for (int i = 0; i < cantidad; i++) {
                iconosPonderados.add(icono);
            }
        }
    }
    
    private String obtenerNombreArchivoDesdeIcono(ImageIcon icono) {
        String path = icono.getDescription();
        if (path == null) {
            path = icono.toString(); // Backup si no hay descripción
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }
    
    //Mostrar los datos del usuario en la interfaz
    private void mostrarDatosUsuario() {
        jLabel2.setText(activeUser.getUsername()); 
        jLabel4.setText(String.valueOf(activeUser.getBalance())); 
    }
    
    private void actualizarIconosLineas() {
        if (lineasActivas >= 2) {
            jButton14.setIcon(new ImageIcon(getClass().getResource("/resources/images/dos-encendido.png")));
        } else {
            jButton14.setIcon(new ImageIcon(getClass().getResource("/resources/images/dos.png")));
        }
        if (lineasActivas >= 3) {
            jButton12.setIcon(new ImageIcon(getClass().getResource("/resources/images/tres-encendido.png")));
        } else {
            jButton12.setIcon(new ImageIcon(getClass().getResource("/resources/images/tres.png")));
        }
        if (lineasActivas >= 4) {
            jButton15.setIcon(new ImageIcon(getClass().getResource("/resources/images/cuatro-encendido.png")));
        } else {
            jButton15.setIcon(new ImageIcon(getClass().getResource("/resources/images/cuatro.png")));
        }
        if (lineasActivas >= 5) {
            jButton17.setIcon(new ImageIcon(getClass().getResource("/resources/images/cinco-encendido.png")));
        } else {
            jButton17.setIcon(new ImageIcon(getClass().getResource("/resources/images/cinco.png")));
        }
        if (lineasActivas >= 6) {
            jButton18.setIcon(new ImageIcon(getClass().getResource("/resources/images/seis-encendido.png")));
        } else {
            jButton18.setIcon(new ImageIcon(getClass().getResource("/resources/images/seis.png")));
        }
        if (lineasActivas >= 7) {
            jButton16.setIcon(new ImageIcon(getClass().getResource("/resources/images/siete-encendido.png")));
        } else {
            jButton16.setIcon(new ImageIcon(getClass().getResource("/resources/images/siete.png")));
        }
    }
    
    //Eleccion aleatoria de iconos en el Tragamonedas
    private ImageIcon obtenerSimboloAleatorio() {
        return iconosPonderados.get(random.nextInt(iconosPonderados.size()));
    }
    
    //Iniciar la accion de aleatoriedad en el Tragamonedas al darle Jugar
    private void inicializarGiroContinuo() {
        for (int col = 0; col < 3; col++) {
            final int columna = col;
            columnaTimers[columna] = new Timer(100, e -> { //Tiempo entre cambio de iconos
                for (int fila = 0; fila < 3; fila++) {
                    carretesBotones[fila][columna].setIcon(obtenerSimboloAleatorio());
                }
            });
            columnaTimers[columna].stop(); // Iniciamos en estado detenido
        }
    }
    
    //Inicializar el tragamonedas
    private void agregarActionListenerJugar() {
        jButton19.addActionListener(e -> {
            // Aquí restamos la apuesta al balance del usuario antes de iniciar el juego
            if (activeUser.getBalance() >= apuestaActual) {
                activeUser.setBalance(activeUser.getBalance() - apuestaActual);
                jLabel4.setText(String.valueOf(activeUser.getBalance())); // Actualizar el balance en la interfaz
                for (int i = 0; i < columnaTimers.length; i++) {
                    columnaTimers[i].setDelay(100);
                    columnaTimers[i].start();
                    columnasDetenidas[i] = false;
                }
                jButton24.setEnabled(true);
                jButton25.setEnabled(true);
                jButton26.setEnabled(true);
                jButton27.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "No tienes suficientes créditos para jugar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jButton24.addActionListener(e -> detenerColumna(0, jButton24));
        jButton25.addActionListener(e -> detenerColumna(1, jButton25));
        jButton26.addActionListener(e -> detenerColumna(2, jButton26));

        jButton27.addActionListener(e -> {
            detenerTodasLasColumnas();
        });
    }
    
    private void detenerTodasLasColumnas() {
        new Thread(() -> {
            try {
                for (int col = 0; col < columnaTimers.length; col++) {
                    if (!columnasDetenidas[col]) { // Solo detener si no está detenido
                        int[] delays = {150, 250, 400, 600};
                        for (int delay : delays) {
                            final int currentCol = col;
                            final int currentDelay = delay;
                            SwingUtilities.invokeLater(() -> columnaTimers[currentCol].setDelay(currentDelay));
                            Thread.sleep(300);
                        }
                        final int currentColFinal = col;
                        SwingUtilities.invokeLater(() -> {
                            columnaTimers[currentColFinal].stop();
                            columnasDetenidas[currentColFinal] = true;
                        });
                        Thread.sleep(500);
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    verificarPremios();
                    jButton24.setEnabled(false);
                    jButton25.setEnabled(false);
                    jButton26.setEnabled(false);
                    jButton27.setEnabled(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void detenerColumna(int columna, JButton boton) {
        if (!columnasDetenidas[columna]) { // Verificar si la columna ya está detenida
            new Thread(() -> {
                try {
                    int[] delays = {150, 250, 400, 600}; // Delays crecientes para frenar
                    for (int delay : delays) {
                        SwingUtilities.invokeLater(() -> {
                            columnaTimers[columna].setDelay(delay);
                        });
                        Thread.sleep(300);
                    }

                    SwingUtilities.invokeLater(() -> {
                        columnaTimers[columna].stop();
                        boton.setEnabled(false); // Desactiva el botón
                        columnasDetenidas[columna] = true; // Marcar la columna como detenida
                        if (todasLasColumnasDetenidas()) {
                            verificarPremios(); // Verificar premios si todas las columnas están detenidas
                            jButton24.setEnabled(false);
                            jButton25.setEnabled(false);
                            jButton26.setEnabled(false);
                            jButton27.setEnabled(false);
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
    
    private boolean todasLasColumnasDetenidas() {
        for (boolean detenida : columnasDetenidas) {
            if (!detenida) {
                return false;
            }
        }
        return true;
    }
    
    //Metodo para la verificacion de premios
    private void verificarLineaPremio(JButton boton1, JButton boton2, JButton boton3, String nombreLinea) {
        Icon icono1 = boton1.getIcon();
        Icon icono2 = boton2.getIcon();
        Icon icono3 = boton3.getIcon();
        if (icono1 != null && icono1.equals(icono2) && icono1.equals(icono3)) {
            String nombreArchivo = obtenerNombreArchivoDesdeIcono((ImageIcon) icono1);
            int multiplicador = tablaPremios.getOrDefault(nombreArchivo, 0);
            int premio = multiplicador * apuestaActual;
            JOptionPane.showMessageDialog(this, "¡Ganaste en " + nombreLinea + "! Premio: " + premio);
            activeUser.setBalance(activeUser.getBalance() + premio);
            jLabel4.setText(String.valueOf(activeUser.getBalance()));
        }
    }

    
    //Verificacion de lineas dependiendo de cuantas estan activas
    private void verificarPremios() {
        if (lineasActivas >= 1) {
            verificarLineaPremio(jButton4, jButton7, jButton8, "Línea 1");
        }
        if (lineasActivas >= 2) {
            verificarLineaPremio(jButton1, jButton2, jButton3, "Línea 2");
        }
        if (lineasActivas >= 3) {
            verificarLineaPremio(jButton9, jButton10, jButton11, "Línea 3");
        }
        if (lineasActivas >= 4) {
            verificarLineaPremio(jButton1, jButton7, jButton11, "Línea 4");
        }
        if (lineasActivas >= 5) {
            verificarLineaPremio(jButton1, jButton4, jButton9, "Línea 5");
        }
        if (lineasActivas >= 6) {
            verificarLineaPremio(jButton2, jButton7, jButton10, "Línea 6");
        }
        if (lineasActivas >= 7) {
            verificarLineaPremio(jButton3, jButton8, jButton11, "Línea 7");
        }
    }
    
    private void guardarResultadoEnDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 1. Insertar en la tabla 'games' (como antes)
            String sqlGames = "INSERT INTO games (user_id, game_type, result, amount, date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtGames = conn.prepareStatement(sqlGames)) {
                pstmtGames.setInt(1, activeUser.getId());
                pstmtGames.setString(2, "Tragamonedas");
                String resultado = (activeUser.getBalance() >= apuestaActual) ? "Ganó" : "Perdió";
                pstmtGames.setString(3, resultado);
                pstmtGames.setDouble(4, activeUser.getBalance());
                pstmtGames.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                pstmtGames.executeUpdate();
            }

            // 2. Actualizar el balance en la tabla 'users'
            String sqlUsers = "UPDATE users SET balance = ? WHERE id = ?";
            try (PreparedStatement pstmtUsers = conn.prepareStatement(sqlUsers)) {
                pstmtUsers.setDouble(1, activeUser.getBalance());
                pstmtUsers.setInt(2, activeUser.getId());
                pstmtUsers.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Resultados del juego y balance actualizados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            System.err.println("Error al guardar el resultado del juego y actualizar el balance: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el resultado del juego y/o actualizar el balance.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton30 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton33 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton34 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton37 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton38 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(34, 40, 44));

        jPanel3.setBackground(new java.awt.Color(21, 25, 28));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("jLabel4");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("jLabel2");

        jButton6.setBackground(new java.awt.Color(21, 25, 28));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/icons8-user-20.png"))); // NOI18N
        jButton6.setBorderPainted(false);
        jButton6.setFocusPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(21, 25, 28));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/icons8-financial-savings-20.png"))); // NOI18N
        jButton5.setBorderPainted(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(40, 40, 40))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cactus.png"))); // NOI18N
        jButton1.setBorderPainted(false);

        jButton2.setBackground(new java.awt.Color(204, 204, 204));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cactus.png"))); // NOI18N
        jButton2.setBorderPainted(false);

        jButton3.setBackground(new java.awt.Color(204, 204, 204));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cactus.png"))); // NOI18N
        jButton3.setBorderPainted(false);

        jButton4.setBackground(new java.awt.Color(204, 204, 204));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/lingotes-de-oro.png"))); // NOI18N
        jButton4.setBorderPainted(false);

        jButton7.setBackground(new java.awt.Color(204, 204, 204));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/lingotes-de-oro.png"))); // NOI18N
        jButton7.setBorderPainted(false);

        jButton8.setBackground(new java.awt.Color(204, 204, 204));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/lingotes-de-oro.png"))); // NOI18N
        jButton8.setBorderPainted(false);

        jButton9.setBackground(new java.awt.Color(204, 204, 204));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/insignia-de-sheriff.png"))); // NOI18N
        jButton9.setBorderPainted(false);

        jButton10.setBackground(new java.awt.Color(204, 204, 204));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/insignia-de-sheriff.png"))); // NOI18N
        jButton10.setBorderPainted(false);

        jButton11.setBackground(new java.awt.Color(204, 204, 204));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/insignia-de-sheriff.png"))); // NOI18N
        jButton11.setBorderPainted(false);

        jPanel4.setBackground(new java.awt.Color(42, 52, 58));

        jButton19.setBackground(new java.awt.Color(42, 52, 58));
        jButton19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton19.setForeground(new java.awt.Color(204, 204, 204));
        jButton19.setText("Jugar");
        jButton19.setBorderPainted(false);

        jButton20.setBackground(new java.awt.Color(42, 52, 58));
        jButton20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton20.setForeground(new java.awt.Color(204, 204, 204));
        jButton20.setText("Apuesta+");
        jButton20.setBorderPainted(false);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setBackground(new java.awt.Color(42, 52, 58));
        jButton21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton21.setForeground(new java.awt.Color(204, 204, 204));
        jButton21.setText("Apuesta-");
        jButton21.setBorderPainted(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setBackground(new java.awt.Color(42, 52, 58));
        jButton22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton22.setForeground(new java.awt.Color(204, 204, 204));
        jButton22.setText("Lineas+");
        jButton22.setBorderPainted(false);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setBackground(new java.awt.Color(42, 52, 58));
        jButton23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton23.setForeground(new java.awt.Color(204, 204, 204));
        jButton23.setText("Lineas-");
        jButton23.setBorderPainted(false);
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(73, 73, 73))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(42, 52, 58));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Premios");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("100");

        jButton30.setBackground(new java.awt.Color(42, 52, 58));
        jButton30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/herradura-Premio.png"))); // NOI18N
        jButton30.setBorderPainted(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("150");

        jButton31.setBackground(new java.awt.Color(42, 52, 58));
        jButton31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/planta-Premio.png"))); // NOI18N
        jButton31.setBorderPainted(false);

        jButton32.setBackground(new java.awt.Color(42, 52, 58));
        jButton32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cactus-Premio.png"))); // NOI18N
        jButton32.setBorderPainted(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("200");

        jButton33.setBackground(new java.awt.Color(42, 52, 58));
        jButton33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/revolver-Premio.png"))); // NOI18N
        jButton33.setBorderPainted(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("250");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("300");

        jButton34.setBackground(new java.awt.Color(42, 52, 58));
        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/insignia-Premio.png"))); // NOI18N
        jButton34.setBorderPainted(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("350");

        jButton35.setBackground(new java.awt.Color(42, 52, 58));
        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/Caballo-Premio.png"))); // NOI18N
        jButton35.setBorderPainted(false);

        jButton36.setBackground(new java.awt.Color(42, 52, 58));
        jButton36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/vaquero-Premio.png"))); // NOI18N
        jButton36.setBorderPainted(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("400");

        jButton37.setBackground(new java.awt.Color(42, 52, 58));
        jButton37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/lingotes-Premio.png"))); // NOI18N
        jButton37.setBorderPainted(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 204, 204));
        jLabel11.setText("450");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton12.setBackground(new java.awt.Color(34, 40, 44));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/tres.png"))); // NOI18N
        jButton12.setBorderPainted(false);

        jButton13.setBackground(new java.awt.Color(34, 40, 44));
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/uno-encendido.png"))); // NOI18N
        jButton13.setBorderPainted(false);

        jButton14.setBackground(new java.awt.Color(34, 40, 44));
        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/dos.png"))); // NOI18N
        jButton14.setBorderPainted(false);

        jButton15.setBackground(new java.awt.Color(34, 40, 44));
        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cuatro.png"))); // NOI18N
        jButton15.setBorderPainted(false);

        jButton16.setBackground(new java.awt.Color(34, 40, 44));
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/siete.png"))); // NOI18N
        jButton16.setBorderPainted(false);

        jButton17.setBackground(new java.awt.Color(34, 40, 44));
        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cinco.png"))); // NOI18N
        jButton17.setBorderPainted(false);

        jButton18.setBackground(new java.awt.Color(34, 40, 44));
        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/seis.png"))); // NOI18N
        jButton18.setBorderPainted(false);

        jPanel6.setBackground(new java.awt.Color(42, 52, 58));

        jButton24.setBackground(new java.awt.Color(42, 52, 58));
        jButton24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton24.setForeground(new java.awt.Color(204, 204, 204));
        jButton24.setText("Detener");
        jButton24.setBorderPainted(false);

        jButton25.setBackground(new java.awt.Color(42, 52, 58));
        jButton25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton25.setForeground(new java.awt.Color(204, 204, 204));
        jButton25.setText("Detener");
        jButton25.setBorderPainted(false);

        jButton26.setBackground(new java.awt.Color(42, 52, 58));
        jButton26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton26.setForeground(new java.awt.Color(204, 204, 204));
        jButton26.setText("Detener");
        jButton26.setBorderPainted(false);

        jButton27.setBackground(new java.awt.Color(42, 52, 58));
        jButton27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton27.setForeground(new java.awt.Color(204, 204, 204));
        jButton27.setText("Todas");
        jButton27.setBorderPainted(false);

        jButton28.setBackground(new java.awt.Color(42, 52, 58));
        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/icons8-casino-100.png"))); // NOI18N
        jButton28.setBorderPainted(false);
        jButton28.setFocusPainted(false);
        jButton28.setFocusable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("Apuesta:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(204, 204, 204));
        jLabel13.setText("50");

        jButton38.setBackground(new java.awt.Color(42, 52, 58));
        jButton38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/icons8-return-35.png"))); // NOI18N
        jButton38.setBorderPainted(false);
        jButton38.setFocusPainted(false);
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(283, 283, 283)
                        .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton38)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jButton25)
                    .addComponent(jButton26)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton38)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        if (evt.getSource() == jButton22) {
            if (lineasActivas < 7) {
                lineasActivas++;
                actualizarIconosLineas();
            }
        }
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        if (evt.getSource() == jButton23) {
            if (lineasActivas > 1) {
                lineasActivas--;
                actualizarIconosLineas();
            }
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        aumentarApuesta();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        disminuirApuesta();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        guardarResultadoEnDB();
        MenuJuegos menuJuegos = new MenuJuegos(activeUser);
        menuJuegos.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton38ActionPerformed

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
            java.util.logging.Logger.getLogger(Tragamonedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tragamonedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tragamonedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tragamonedas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tragamonedas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables
}
