package UI;

import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;
import pojos.*;
import receiveData.*;

import static UI.DoctorApp.menuDoctor;
import static UI.DoctorApp.selectPatient;

public class Doctor {
    Socket socket = null;

    public void register() throws IOException {
        // Crear un objeto Doctor y obtener los datos del doctor
        Scanner scanner = new Scanner(System.in);
        pojos.Doctor doctor = new pojos.Doctor();

        System.out.println("Enter your name: ");
        doctor.setName(scanner.nextLine());

        System.out.println("Enter your surname: ");
        doctor.setSurname(scanner.nextLine());

        System.out.println("Enter your DNI: ");
        doctor.setDNI(scanner.nextLine());

        System.out.println("Enter your date of birth (YYYY-MM-DD): ");
        String dateOfBirthStr = scanner.nextLine();
        Date dateOfBirth = Date.valueOf(dateOfBirthStr);
        doctor.setBirthDate(dateOfBirth);

        System.out.println("Enter your sex (M/F): ");
        doctor.setSex(scanner.nextLine());

        System.out.println("Enter your email: ");
        doctor.setEmail(scanner.nextLine());

        // Ahora, usar la clase SendDataViaNetwork para enviar los datos del paciente al servidor
        SendDataViaNetwork sendData = new SendDataViaNetwork(socket);
        sendData.sendDoctor(doctor);

        System.out.println("Registration successful!");
    }


    public void logIn() throws IOException {
        // Crear un objeto Scanner para obtener las credenciales
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        // Usar la clase SendDataViaNetwork para enviar las credenciales al servidor
        SendDataViaNetwork sendData = new SendDataViaNetwork(socket);
        sendData.sendString(username);  // Enviar nombre de usuario
        sendData.sendString(password);  // Enviar contraseña

        // Ahora, recibir la respuesta del servidor sobre el login
        ReceiveDataViaNetwork receiveData = new ReceiveDataViaNetwork(socket);
        int loginResponse = receiveData.receiveInt();  // Recibir respuesta del servidor (1 para éxito, 0 para error)

        if (loginResponse == 1) {
            System.out.println("Login successful!");
            selectPatient();  // llamar a select patient de doctor app
        } else {
            System.out.println("Invalid credentials, please try again.");

        }


    }
    public void viewPatients() throws IOException {
        // Recibir la lista de pacientes desde el servidor
        ReceiveDataViaNetwork receiveData = new ReceiveDataViaNetwork(socket);

        // Llamamos al método `receivePatientList()` para obtener todos los pacientes
        List<Patient> patients = receiveData.receivePatientList();

        // Mostrar la lista de pacientes con su ID
        System.out.println("This is the list of patients:");
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.println((i + 1) + ". ID: " + patient.getId() + " - " + patient.getName());
        }
    }
    public void viewPatientData(int patientId) throws IOException {
        ReceiveDataViaNetwork receiveDataViaNetwork= new ReceiveDataViaNetwork(socket);

    }
    public void addFeedback(int patientId) throws IOException {

    }
    public void viewRecordedSignal(int patientId) throws IOException {

    }
    public void changePatientData (int patientId) throws IOException {

    }
}
