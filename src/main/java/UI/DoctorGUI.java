package UI;

import receiveData.ReceiveDataViaNetwork;
import receiveData.SendDataViaNetwork;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class DoctorGUI extends JFrame {

    // Contexto de conexión (ahora se inicializa más tarde)
    private DoctorClientContext context;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Paneles
    private JPanel connectPanel; // <--- Nuevo panel
    private JPanel authPanel;
    private JPanel selectPatientPanel;
    private JPanel menuPanel;

    // Componentes de connectPanel
    private JTextField ipField;

    // Componentes de selectPatient
    private JTextArea patientListArea;
    private JTextField patientIdField;

    // Estado
    private Integer currentPatientId = null;

    // Estética
    private static final Color BG_COLOR = new Color(230, 245, 245);    // turquesa muy claro
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(190, 215, 215);
    private static final Color TEXT_DARK = new Color(25, 40, 40);


    private static final Color BLUE_BUTTON = new Color(86, 132, 225);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 26);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 15);

    public DoctorGUI() {
        super("Telemedicine - Doctor");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(900, 600));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);

        // 1. Crear los paneles
        connectPanel = createConnectPanel(); // <--- Creamos el panel de conexión
        authPanel = createAuthPanel();
        selectPatientPanel = createSelectPatientPanel();
        menuPanel = createMenuPanel();

        // 2. Añadirlos al CardLayout
        mainPanel.add(connectPanel, "CONNECT");
        mainPanel.add(authPanel, "AUTH");
        mainPanel.add(selectPatientPanel, "SELECT");
        mainPanel.add(menuPanel, "MENU");

        setContentPane(mainPanel);

        // 3. Mostrar primero la pantalla de conexión
        cardLayout.show(mainPanel, "CONNECT");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorGUI::new);
    }

    // ===================== Helpers de estilo =====================

    private JPanel wrapCard(JPanel inner, Dimension prefSize) {
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG_COLOR);

        inner.setBackground(CARD_COLOR);
        inner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        if (prefSize != null) {
            inner.setPreferredSize(prefSize);
        }

        background.add(inner);
        return background;
    }

    private void styleFlatButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(243, 250, 250));
        b.setForeground(TEXT_DARK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // ===================== PANTALLA 0: CONEXIÓN AL SERVIDOR =====================

    private JPanel createConnectPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to Telemedicine");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        JLabel ipLabel = new JLabel("Enter Server IP Address:");
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo de texto con "localhost" por defecto
        ipField = new JTextField("localhost");
        ipField.setMaximumSize(new Dimension(250, 30));
        ipField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleFlatButton(connectButton);

        connectButton.addActionListener(e -> attemptConnection());

        panel.add(Box.createVerticalStrut(15));
        panel.add(title);
        panel.add(Box.createVerticalStrut(25));
        panel.add(ipLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ipField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(connectButton);
        panel.add(Box.createVerticalStrut(10));

        // Envolvemos en un fondo de color para centrar la “tarjeta”
        return wrapCard(panel, new Dimension(380, 300));
    }

    private void attemptConnection() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an IP address.");
            return;
        }

        try {
            // Intentamos conectar creando el contexto
            this.context = new DoctorClientContext(ip, 8888);

            // Si no da error, pasamos a la siguiente pantalla
            JOptionPane.showMessageDialog(this, "Connected to server successfully!");
            cardLayout.show(mainPanel, "AUTH");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error connecting to server at " + ip + ":\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== PANTALLA 1: LOGIN / REGISTER =====================

    private JPanel createAuthPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Doctor Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Register");

        styleFlatButton(loginButton);
        styleFlatButton(registerButton);

        loginButton.addActionListener(e -> showLoginForm());
        registerButton.addActionListener(e -> showRegisterForm());

        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(25));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(15));

        return wrapCard(panel, new Dimension(380, 300));
    }

    private void showRegisterForm() {
        JDialog dialog = new JDialog(this, "Register Doctor", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(CARD_COLOR);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos del Doctor
        JTextField nameField = new JTextField(15);
        JTextField surnameField = new JTextField(15);
        JTextField dniField = new JTextField(15);
        JTextField dobField = new JTextField(15); // YYYY-MM-DD
        JTextField sexField = new JTextField(5);
        JTextField emailField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Name:"), gbc); gbc.gridx = 1; content.add(nameField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Surname:"), gbc); gbc.gridx = 1; content.add(surnameField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("DNI:"), gbc); gbc.gridx = 1; content.add(dniField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Birth date:"), gbc); gbc.gridx = 1; content.add(dobField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Sex (M/F):"), gbc); gbc.gridx = 1; content.add(sexField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Email:"), gbc); gbc.gridx = 1; content.add(emailField, gbc);
        row++; gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Password:"), gbc); gbc.gridx = 1; content.add(passwordField, gbc);

        JButton registerBtn = new JButton("Register");
        styleFlatButton(registerBtn); // Usamos tu estilo definido

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(registerBtn, gbc);

        registerBtn.addActionListener(ev -> {
            // Recoger datos
            String name = nameField.getText();
            String surname = surnameField.getText();
            String dni = dniField.getText();
            String dob = dobField.getText();
            String sex = sexField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Bloquear UI
            registerBtn.setEnabled(false);
            registerBtn.setText("Registering...");

            // Hilo secundario
            new Thread(() -> {
                try {
                    boolean ok = context.getDoctorUI().registerFromGUI(
                            name, surname, dni, dob, sex, email, password,
                            context.getSocket(), context.getSendData(), context.getReceiveData()
                    );

                    SwingUtilities.invokeLater(() -> {
                        registerBtn.setEnabled(true);
                        registerBtn.setText("Register");

                        if (ok) {
                            JOptionPane.showMessageDialog(dialog, "Doctor registered successfully. Logging in...");
                            dialog.dispose();

                            // === LOGICA DE RECONEXION ===
                            try {
                                // 1. Cerrar socket viejo
                                context.getSocket().close();

                                // 2. Recuperar IP del campo de texto principal
                                String ip = ipField.getText().trim();
                                if(ip.isEmpty()) ip = "localhost";

                                // 3. Crear conexión NUEVA
                                context = new DoctorClientContext(ip, 8888);

                                // 4. Mostrar Login inmediatamente
                                showLoginForm();

                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(mainPanel,
                                        "Registered, but failed to reconnect.\n" + e.getMessage(),
                                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                                cardLayout.show(mainPanel, "AUTH");
                            }
                            // ============================

                        } else {
                            JOptionPane.showMessageDialog(dialog, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        registerBtn.setEnabled(true);
                        registerBtn.setText("Register");
                        JOptionPane.showMessageDialog(dialog, "Connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        });

        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showLoginForm() {
        JDialog dialog = new JDialog(this, "Log in", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(CARD_COLOR);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField emailField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        content.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        content.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        content.add(passwordField, gbc);

        JButton loginBtn = new JButton("Log in");
        styleFlatButton(loginBtn); // Usamos tu estilo definido en DoctorGUI

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(loginBtn, gbc);

        loginBtn.addActionListener(ev -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // 1. Desactivar botón y cambiar texto
            loginBtn.setEnabled(false);
            loginBtn.setText("Connecting...");

            // 2. Hilo secundario para la red
            new Thread(() -> {
                try {
                    boolean ok = context.getDoctorUI().logInFromGUI(
                            email,
                            password,
                            context.getSocket(),
                            context.getSendData(),
                            context.getReceiveData()
                    );

                    // 3. Volver a Swing para actualizar UI
                    SwingUtilities.invokeLater(() -> {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Log in");

                        if (ok) {
                            JOptionPane.showMessageDialog(dialog, "Log in successful");
                            dialog.dispose();
                            goToSelectPatientScreen(); // Metodo específico del Doctor
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Incorrect user or password",
                                    "Login error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Log in");
                        JOptionPane.showMessageDialog(dialog,
                                "Connection error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start(); // IMPORTANTE: Iniciar hilo
        });

        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ===================== PANTALLA 2: SELECT PATIENT =====================

    private JPanel createSelectPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        JLabel label = new JLabel("Select one of your patients");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(TEXT_DARK);

        patientListArea = new JTextArea();
        patientListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(patientListArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(CARD_COLOR);
        bottomPanel.add(new JLabel("Enter patient ID:"));
        patientIdField = new JTextField(10);
        bottomPanel.add(patientIdField);
        JButton selectButton = new JButton("Select");
        styleFlatButton(selectButton);
        bottomPanel.add(selectButton);

        selectButton.addActionListener(e -> selectPatient());

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return wrapCard(panel, new Dimension(600, 450));
    }

    private void goToSelectPatientScreen() {
        try {
            ReceiveDataViaNetwork r = context.getReceiveData();
            String patientList = r.receiveString();
            patientListArea.setText(patientList);
            patientIdField.setText("");
            cardLayout.show(mainPanel, "SELECT");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error receiving patient list: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectPatient() {
        try {
            String idText = patientIdField.getText();
            if (idText == null || idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a patient ID");
                return;
            }
            int id = Integer.parseInt(idText);
            currentPatientId = id;

            SendDataViaNetwork s = context.getSendData();
            s.sendInt(currentPatientId);

            JOptionPane.showMessageDialog(this, "Patient " + currentPatientId + " selected");
            cardLayout.show(mainPanel, "MENU");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error sending patient ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== PANTALLA 3: MENU 4 OPCIONES =====================

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(420, 320));

        JLabel title = new JLabel("Doctor menu - patient selected");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);

        JButton viewDetailsButton = new JButton("View patient details");
        JButton updateFeedbackButton = new JButton("Update feedback");
        JButton viewSignalButton = new JButton("View recorded signal");

        styleFlatButton(viewDetailsButton);
        styleFlatButton(updateFeedbackButton);
        styleFlatButton(viewSignalButton);

        viewDetailsButton.addActionListener(e -> onViewPatientDetails());
        updateFeedbackButton.addActionListener(e -> onUpdateFeedback());
        viewSignalButton.addActionListener(e -> onViewSignal());

        panel.add(Box.createVerticalStrut(15));
        panel.add(title);
        panel.add(Box.createVerticalStrut(25));
        panel.add(viewDetailsButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(updateFeedbackButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(viewSignalButton);
        panel.add(Box.createVerticalStrut(10));

        return wrapCard(panel, null);
    }

    // ===================== MÉTODOS LÓGICA (NO CAMBIADOS) =====================

    private void onViewPatientDetails() {
        if (currentPatientId == null) {
            JOptionPane.showMessageDialog(this, "No patient selected");
            return;
        }
        try {
            String info = context.getDoctorUI().viewPatientDetailsAndMedicalInfoFromGUI(
                    currentPatientId,
                    context.getSocket(),
                    context.getReceiveData(),
                    context.getSendData()
            );

            JTextArea area = new JTextArea(info);
            area.setEditable(false);
            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "Patient details and medical information",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error viewing patient: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onViewSignal() {
        if (currentPatientId == null) {
            JOptionPane.showMessageDialog(this, "No patient selected");
            return;
        }

        new Thread(() -> {
            try {
                context.getDoctorUI().viewRecordedSignalFromGUI(
                        currentPatientId,
                        context.getSocket(),
                        context.getReceiveData(),
                        context.getSendData(),
                        DoctorGUI.this
                );

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        DoctorGUI.this,
                        "Error viewing recorded signal: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();
    }

    private void onUpdateFeedback() {
        if (currentPatientId == null) {
            JOptionPane.showMessageDialog(this, "No patient selected");
            return;
        }

        new Thread(() -> {
            try {
                context.getDoctorUI().selectAndUpdateFeedbackGUI(
                        currentPatientId,
                        context.getSocket(),
                        context.getReceiveData(),
                        context.getSendData(),
                        DoctorGUI.this
                );
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        DoctorGUI.this,
                        "Error updating feedback: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();
    }

}
