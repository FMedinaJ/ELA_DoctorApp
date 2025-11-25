package UI;

import receiveData.ReceiveDataViaNetwork;
import receiveData.SendDataViaNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DoctorGUI extends JFrame {
    private DoctorClientContext context;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Paneles
    private JPanel authPanel;
    private JPanel selectPatientPanel;
    private JPanel menuPanel;

    // Componentes de selectPatient
    private JTextArea patientListArea;
    private JTextField patientIdField;

    // Estado
    private Integer currentPatientId = null;

    public DoctorGUI(DoctorClientContext context) {
        super("Telemedicine - Doctor");
        this.context = context;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        authPanel = createAuthPanel();
        selectPatientPanel = createSelectPatientPanel();
        menuPanel = createMenuPanel();

        mainPanel.add(authPanel, "AUTH");
        mainPanel.add(selectPatientPanel, "SELECT");
        mainPanel.add(menuPanel, "MENU");

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "AUTH"); // primera pantalla: log in / register

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DoctorClientContext context = new DoctorClientContext("localhost", 8000);
                new DoctorGUI(context);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error connecting to server: " + e.getMessage(),
                        "Connection error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ===================== PANTALLA 1: LOGIN / REGISTER =====================

    private JPanel createAuthPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Doctor - Telemedicine");
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
                    goToSelectPatientScreen(); // siguiente pantalla: lista de pacientes
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
                    goToSelectPatientScreen(); // siguiente pantalla
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
        // Después de un login/register correcto, el servidor envía la lista de pacientes
        try {
            ReceiveDataViaNetwork r = context.getReceiveData();
            String patientList = r.receiveString();  // mismo que antes en DoctorApp.selectPatient
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
            s.sendInt(currentPatientId);  // igual que antes en DoctorApp.selectPatient

            JOptionPane.showMessageDialog(this, "Patient " + currentPatientId + " selected");
            cardLayout.show(mainPanel, "MENU"); // Pasamos a la pantalla con las 4 opciones

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid ID format",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error sending patient ID: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        //updateFeedbackButton.addActionListener(e -> onUpdateFeedback());
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
            String info = context.getDoctorUI().viewPatientDataFromGUI(
                    currentPatientId,
                    context.getSocket(),
                    context.getReceiveData(),
                    context.getSendData()
            );
            JTextArea area = new JTextArea(info);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Patient details", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error viewing patient: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this,
                    "Error changing patient data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Telemedicine- Doctor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Crear panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Crear menú de opciones
        JButton loginButton = new JButton("Log in ");
        JButton registerButton = new JButton("Register");
        JButton selectPatientButton = new JButton("Select patient");
        JButton viewPatientButton = new JButton("View patient data");
        JButton visualizeSignalButton = new JButton("Visualize signal ");
        JButton newfeedbackButton = new JButton("Update feedback");
        JButton modifyPatientButton = new JButton("Modify patient info");


        // Agregar los botones al panel
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(selectPatientButton);
        panel.add(viewPatientButton);
        panel.add(visualizeSignalButton);
        panel.add(newfeedbackButton);
        panel.add(modifyPatientButton);

        // Acciones de los botones
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRegisterForm();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLogInForm();
            }
        });


        selectPatientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSelectPatient();
            }
        });

        viewPatientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showViewPatient();
            }
        });

        visualizeSignalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showVisualizeSignal();
            }
        });
        newfeedbackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showNewFeedback();
            }
        });
        modifyPatientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showModifyData();
            }
        });

        // Agregar panel a la ventana
        frame.add(panel);
        frame.setVisible(true);
    }

    // Métodos para mostrar cada pantalla (opciones del menú)
    private static void showRegisterForm() {
        JFrame frame = new JFrame("Register");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Surname:"));
        JTextField surnameField = new JTextField();
        panel.add(surnameField);

        panel.add(new JLabel("Dni:"));
        JTextField dniField = new JTextField();
        panel.add(dniField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Sex:"));
        JTextField genderField = new JTextField();
        panel.add(genderField);

        panel.add(new JLabel("Birth date:"));
        JTextField dobField = new JTextField();
        panel.add(dobField);



        JButton sendButton = new JButton("Send");
        panel.add(sendButton);
        sendButton.addActionListener(e -> {
            System.out.println("Register Doctor: " + nameField.getText());
        });
        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showLogInForm() {
        JFrame frame = new JFrame("Log in:");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        // ns si es asi
        panel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Entrar");
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            System.out.println("Doctor log in: " + emailField.getText());
        });

        frame.add(panel);
        frame.setVisible(true);


    }

    private static void showSelectPatient() {

    }

    private static void showViewPatient() {
    }

    private static void showVisualizeSignal() {

    }

    private static void showNewFeedback() {
    }


    private static void showModifyData() {

    }**/
}


