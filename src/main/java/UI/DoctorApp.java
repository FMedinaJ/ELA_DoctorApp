package UI;

import receiveData.ReceiveDataViaNetwork;
import receiveData.SendDataViaNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class DoctorApp {
    private static Socket socket;
    private static SendDataViaNetwork sendDataViaNetwork;
    private static ReceiveDataViaNetwork receiveDataViaNetwork;
    private static Doctor doctor;
    public static void main(String[] args) {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        // Establecer conexión con el servidor
        while (running) {
            String ipAddress = Utilities.readString("Enter the IP address of the server to connect to:\n");
            try {
                socket = new Socket(ipAddress, 8000);
                sendDataViaNetwork = new SendDataViaNetwork(socket);
                receiveDataViaNetwork = new ReceiveDataViaNetwork(socket);
                sendDataViaNetwork.sendInt(2);  // Se asume que se está enviando un código para verificar la conexión
                String message = receiveDataViaNetwork.receiveString();
                System.out.println(message);

                if (message.equals("DOCTOR")) {
                    // Proceder con las opciones del paciente
                    showDoctorMenu();
                } else {
                    System.out.println("Server response invalid. Try again.");
                }
            } catch (IOException e) {
                System.out.println("Connection failed: " + e.getMessage());
                running = false;  // Salir si no se puede conectar
            }
        }
    }
    public static void showDoctorMenu() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        // Crear un único objeto Patient que será usado en todos los casos
        UI.Doctor doctor = new UI.Doctor();

        while (running) {
            System.out.println("1- Log in");
            System.out.println("2- Sign up");
            System.out.println("0- Exit");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    try {
                        doctor.logIn();  // Llama al método logIn() en la clase doctor
                        selectPatient();
                    } catch (IOException e) {
                        System.out.println("Error during login: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        doctor.register();  // Llama al método register() en la clase doctor
                    } catch (IOException e) {
                        System.out.println("Error during registration: " + e.getMessage());
                    }
                    break;
                case 0:
                    System.out.println("Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    public static void selectPatient() {
        try {
            // Enviar solicitud para seleccionar un paciente
            doctor.viewPatients(); // Este método mostrará la lista de pacientes
            Scanner scanner = new Scanner(System.in);
            System.out.print("Choose the patient's ID you want to work with: ");
            int patientId = scanner.nextInt();  // Leemos el ID del paciente seleccionado

            // Enviar el ID al servidor para obtener los datos del paciente
            sendDataViaNetwork.sendInt(patientId);
            String patientData = receiveDataViaNetwork.receiveString();  // Recibir datos del paciente desde el servidor
            System.out.println("Patient Data: " + patientData);
            menuDoctor(patientId);
        } catch (IOException e) {
            System.out.println("Error selecting patient: " + e.getMessage());
        }
    }

    public static void menuDoctor(int patientId) throws IOException {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        while(running) {
            System.out.println("Welcome to the Doctor App!");
            System.out.println("Please choose an option");
            System.out.println("View patient's details");
            System.out.println("Add feedback");
            System.out.println("View recorded signal");
            System.out.println("Change patient data");
            int option = scanner.nextInt();

         switch(option) {
             case 1:
                 doctor.viewPatientData(patientId);
                 break;
             case 2:
                 doctor.addFeedback(patientId);
                 break;
             case 3:
                 doctor.viewRecordedSignal(patientId);
                 break;
             case 4:
                 doctor.changePatientData(patientId);
                 break;
             case 0:
                 running = false;
                 break;
             default:
                 System.out.println("Invalid option, try again.");

         }

         }

        }

    }

