package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoctorGUI {
    public static void main(String[] args) {
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

        panel.add(new JLabel("Gender:"));
        JTextField genderField = new JTextField();
        panel.add(genderField);

        panel.add(new JLabel("Birth date:"));
        JTextField dobField = new JTextField();
        panel.add(dobField);


        JButton sendButton = new JButton("Send");
        panel.add(sendButton);
        sendButton.addActionListener(e -> {
            System.out.println("Regsiter Doctor: " + nameField.getText());
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

    }
}


