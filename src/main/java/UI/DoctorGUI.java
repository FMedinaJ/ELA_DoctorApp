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

    public DoctorGUI() {
        super("Telemedicine - Doctor");
        // Nota: Ya no pedimos el 'context' en el constructor

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

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
        SwingUtilities.invokeLater(() -> {
            // Ya no conectamos aquí, solo lanzamos la interfaz
            new DoctorGUI();
        });
    }

    // ===================== PANTALLA 0: CONEXIÓN AL SERVIDOR =====================

    private JPanel createConnectPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to Telemedicine");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel ipLabel = new JLabel("Enter Server IP Address:");
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo de texto con "localhost" por defecto
        ipField = new JTextField("localhost");
        ipField.setMaximumSize(new Dimension(200, 30));
        ipField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectButton.addActionListener(e -> attemptConnection());

        panel.add(Box.createVerticalStrut(50));
        panel.add(title);
        panel.add(Box.createVerticalStrut(40));
        panel.add(ipLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ipField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(connectButton);

        return panel;
    }

    private void attemptConnection() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an IP address.");
            return;
        }

        try {
            // Intentamos conectar creando el contexto
            // Asumimos puerto 8888 fijo, pero podrías poner otro campo para el puerto
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
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Register");

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> showLoginForm());
        registerButton.addActionListener(e -> showRegisterForm());

        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(40));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);

        return panel;
    }

    private void showLoginForm() {
        JDialog dialog = new JDialog(this, "Log in", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(3, 2));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);

        JButton loginBtn = new JButton("Log in");
        dialog.add(new JLabel());
        dialog.add(loginBtn);

        loginBtn.addActionListener(ev -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            try {
                boolean ok = context.getDoctorUI().logInFromGUI(
                        email,
                        password,
                        context.getSocket(),
                        context.getSendData(),
                        context.getReceiveData()
                );
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Log in successful");
                    dialog.dispose();
                    goToSelectPatientScreen();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Incorrect user or password",
                            "Login error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Connection error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRegisterForm() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(450, 350);
        dialog.setLayout(new GridLayout(8, 2));

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField dniField = new JTextField();
        JTextField dobField = new JTextField();  // YYYY-MM-DD
        JTextField sexField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        dialog.add(new JLabel("Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Surname:")); dialog.add(surnameField);
        dialog.add(new JLabel("DNI:")); dialog.add(dniField);
        dialog.add(new JLabel("Birth date (YYYY-MM-DD):")); dialog.add(dobField);
        dialog.add(new JLabel("Sex (M/F):")); dialog.add(sexField);
        dialog.add(new JLabel("Email:")); dialog.add(emailField);
        dialog.add(new JLabel("Password:")); dialog.add(passwordField);

        JButton registerBtn = new JButton("Register");
        dialog.add(new JLabel());
        dialog.add(registerBtn);

        registerBtn.addActionListener(ev -> {
            try {
                boolean ok = context.getDoctorUI().registerFromGUI(
                        nameField.getText(),
                        surnameField.getText(),
                        dniField.getText(),
                        dobField.getText(),
                        sexField.getText(),
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        context.getSocket(),
                        context.getSendData(),
                        context.getReceiveData()
                );
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Doctor registered successfully");
                    dialog.dispose();
                    goToSelectPatientScreen();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Registration failed",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Connection error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ===================== PANTALLA 2: SELECT PATIENT =====================

    private JPanel createSelectPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Select one of your patients");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        patientListArea = new JTextArea();
        patientListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(patientListArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Enter patient ID:"));
        patientIdField = new JTextField(10);
        bottomPanel.add(patientIdField);
        JButton selectButton = new JButton("Select");
        bottomPanel.add(selectButton);

        selectButton.addActionListener(e -> selectPatient());

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
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

        JLabel title = new JLabel("Doctor menu - patient selected");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton viewDetailsButton = new JButton("View patient details");
        JButton updateFeedbackButton = new JButton("Update feedback");
        JButton viewSignalButton = new JButton("View recorded signal");
        JButton modifyDataButton = new JButton("Change patient data");

        viewDetailsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateFeedbackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewSignalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifyDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        viewDetailsButton.addActionListener(e -> onViewPatientDetails());
        // updateFeedbackButton.addActionListener(e -> onUpdateFeedback());
        // viewSignalButton.addActionListener(e -> onViewSignal());
        modifyDataButton.addActionListener(e -> onChangePatientData());

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(viewDetailsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(updateFeedbackButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewSignalButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(modifyDataButton);

        return panel;
    }


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


    private void onChangePatientData() {
        if (currentPatientId == null) {
            JOptionPane.showMessageDialog(this, "No patient selected");
            return;
        }
        try {
            String result = context.getDoctorUI().changePatientDataFromGUI(
                    currentPatientId,
                    context.getSocket(),
                    context.getReceiveData(),
                    context.getSendData(),
                    this
            );
            JOptionPane.showMessageDialog(this, result);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error changing patient data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}