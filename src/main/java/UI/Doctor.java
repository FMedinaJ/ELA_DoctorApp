package UI;

import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;
import pojos.*;
import receiveData.*;
import sun.misc.Signal;

import javax.sound.midi.Soundbank;

import static UI.DoctorApp.menuDoctor;
import static UI.DoctorApp.selectPatient;

public class Doctor {
    Socket socket = null;
    SendDataViaNetwork sendDataViaNetwork;
    ReceiveDataViaNetwork receiveDataViaNetwork;

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
        SendDataViaNetwork sendData = new SendDataViaNetwork(socket);
        ReceiveDataViaNetwork receiveDataViaNetwork = new ReceiveDataViaNetwork(socket);

        // Solicitar los datos del paciente
        sendData.sendInt(patientId);

        // Recibir los detalles del paciente desde el servidor
        String patientData = receiveDataViaNetwork.receiveString();
        System.out.println("Patient Data: " + patientData);

    }
    public void addFeedback(int patientId) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your feedback: ");
        String feedback = scanner.nextLine();
        sendDataViaNetwork.sendInt(patientId); //enviar id del paciente
        sendDataViaNetwork.sendString(feedback);
        String feedbackData = receiveDataViaNetwork.receiveString();
        System.out.println("Feedback: " + feedbackData);




    }
    public void viewRecordedSignal(int patientId) throws IOException {
       sendDataViaNetwork.sendInt(patientId);

    }
    public void changePatientData(int patientId) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Mostrar las opciones al doctor
        System.out.println("What information would you like to change?");
        System.out.println("1 - Name");
        System.out.println("2 - Surname");
        System.out.println("3 - Phone");
        System.out.println("4 - Email");
        System.out.println("5 - Insurance");
        System.out.println("0 - Exit");

        // Leer la opción seleccionada por el doctor
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consumir la nueva línea

        // Variables para los nuevos valores
        String newName = null, newSurname = null, newPhone = null, newEmail = null;
        Integer newInsurance = null;  // Usamos Integer para los campos int en caso de que no se ingrese un valor

        // Condicionales para manejar la opción seleccionada
        switch (choice) {
            case 1:
                // Solicitar nuevo nombre
                System.out.print("Enter new name: ");
                newName = scanner.nextLine();
                break;
            case 2:
                // Solicitar nuevo apellido
                System.out.print("Enter new surname: ");
                newSurname = scanner.nextLine();
                break;
            case 3:
                // Solicitar nuevo teléfono (int)
                System.out.print("Enter new phone number: ");
                newPhone = scanner.nextLine();
                // Verificar que el teléfono sea un número válido
                try {
                    Integer.parseInt(newPhone);  // Convertir a int
                } catch (NumberFormatException e) {
                    System.out.println("Invalid phone number. Please enter a valid number.");
                    return;  // Salir del método si el número no es válido
                }
                break;
            case 4:
                // Solicitar nuevo email
                System.out.print("Enter new email: ");
                newEmail = scanner.nextLine();
                break;
            case 5:
                // Solicitar nuevo seguro (int)
                System.out.print("Enter new insurance number: ");
                String newInsuranceStr = scanner.nextLine();
                // Verificar que el seguro sea un número válido
                try {
                    newInsurance = Integer.parseInt(newInsuranceStr);  // Convertir a int
                } catch (NumberFormatException e) {
                    System.out.println("Invalid insurance number. Please enter a valid number.");
                    return;  // Salir del método si el número no es válido
                }
                break;
            case 0:
                // Salir
                System.out.println("Exiting...");
                return;  // Salir del método
            default:
                System.out.println("Invalid choice, please try again.");
                return;
        }

        // Enviar el ID del paciente al servidor
        sendDataViaNetwork.sendInt(patientId);  // Enviar el ID del paciente

        // Enviar solo los campos modificados (si no son null)
        if (newName != null) {
            sendDataViaNetwork.sendString(newName);  // Solo enviar el nuevo nombre si fue modificado
        } else {
            sendDataViaNetwork.sendString("");  // Enviar una cadena vacía si no se modificó
        }

        if (newSurname != null) {
            sendDataViaNetwork.sendString(newSurname);  // Solo enviar el nuevo apellido si fue modificado
        } else {
            sendDataViaNetwork.sendString("");  // Enviar una cadena vacía si no se modificó
        }

        if (newPhone != null) {
            sendDataViaNetwork.sendString(newPhone);  // Solo enviar el nuevo teléfono si fue modificado
        } else {
            sendDataViaNetwork.sendString("");  // Enviar una cadena vacía si no se modificó
        }

        if (newEmail != null) {
            sendDataViaNetwork.sendString(newEmail);  // Solo enviar el nuevo email si fue modificado
        } else {
            sendDataViaNetwork.sendString("");  // Enviar una cadena vacía si no se modificó
        }

        if (newInsurance != null) {
            sendDataViaNetwork.sendInt(newInsurance);  // Solo enviar el nuevo seguro si fue modificado
        } else {
            sendDataViaNetwork.sendInt(-1);  // Enviar -1 si no se modificó
        }

        // Recibir la respuesta del servidor
        String response = receiveDataViaNetwork.receiveString();
        System.out.println(response);  // Mostrar la respuesta del servidor
    }
}
