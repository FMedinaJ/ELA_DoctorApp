package UI;

import pojos.Patient;
import receiveData.ReceiveDataViaNetwork;
import receiveData.SendDataViaNetwork;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import pojos.Signal;

public class DoctorApp {
    public static void main(String[] args) {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        // Establecer conexión con el servidor
        while (running) {
            String ipAddress = Utilities.readString("Enter the IP address of the server to connect to:\n");
            try {
                Socket socket = new Socket("localhost", 8000);
                SendDataViaNetwork sendDataViaNetwork = new SendDataViaNetwork(socket);
                ReceiveDataViaNetwork receiveDataViaNetwork = new ReceiveDataViaNetwork(socket);
                sendDataViaNetwork.sendInt(1);  // Se asume que se está enviando un código para verificar la conexión
                String message = receiveDataViaNetwork.receiveString();
                System.out.println(message);

                if (message.equals("DOCTOR")) {
                    // Proceder con las opciones del paciente
                    showDoctorMenu(socket, sendDataViaNetwork, receiveDataViaNetwork);
                } else {
                    System.out.println("Server response invalid. Try again.");
                }
            } catch (IOException e) {
                System.out.println("Connection failed: " + e.getMessage());
                running = false;  // Salir si no se puede conectar
            }
        }
    }
    public static void showDoctorMenu(Socket socket, SendDataViaNetwork sendDataViaNetwork, ReceiveDataViaNetwork receiveDataViaNetwork) {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        // Crear un único objeto Patient que será usado en todos los casos
        DoctorUI doctorUI = new DoctorUI();

        while (running) {
            System.out.println("1- Log in");
            System.out.println("2- Sign up");
            System.out.println("0- Exit");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    try {
                        doctorUI.logIn(socket,sendDataViaNetwork,receiveDataViaNetwork);  // Llama al método logIn() en la clase doctor
                    } catch (IOException e) {
                        System.out.println("Error during login: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        doctorUI.register(socket,sendDataViaNetwork,receiveDataViaNetwork);  // Llama al método register() en la clase doctor
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
    public static void selectPatient(Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork,SendDataViaNetwork sendDataViaNetwork) throws IOException {
        try {
            sendDataViaNetwork.sendInt(1);
            String patientList = receiveDataViaNetwork.receiveString();
            System.out.println(patientList);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose an id: ");
            int id_patient = scanner.nextInt();

            sendDataViaNetwork.sendInt(id_patient);
            String patientData= receiveDataViaNetwork.receiveString();// recibir los datos del paciente
            System.out.println(patientData);//comprobar que está seleccionado el paciente correco
            menuDoctor(id_patient,receiveDataViaNetwork,sendDataViaNetwork,socket);
        } catch (IOException e) {
            System.out.println("Error selecting patient: " + e.getMessage());
        }
    }

    public static void menuDoctor(int patientId, ReceiveDataViaNetwork receiveDataViaNetwork,SendDataViaNetwork sendDataViaNetwork, Socket socket) throws IOException {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        DoctorUI doctorUI = new DoctorUI();
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
                 doctorUI.viewPatientData(patientId,socket,receiveDataViaNetwork,sendDataViaNetwork);
                 break;
             case 2:
                 doctorUI.addFeedback(patientId, socket,receiveDataViaNetwork,sendDataViaNetwork);
                 break;
             case 3:
                 doctorUI.viewRecordedSignal(patientId,socket,receiveDataViaNetwork,sendDataViaNetwork);
                 break;
             case 4:
                 doctorUI.changePatientData(patientId,socket,receiveDataViaNetwork,sendDataViaNetwork);
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

