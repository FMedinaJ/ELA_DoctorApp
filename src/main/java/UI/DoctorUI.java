package UI;

import pojos.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import receiveData. *;

public class DoctorUI {
    Socket socket2 = null;

    public void register(Socket socket, SendDataViaNetwork sendDataViaNetwork, ReceiveDataViaNetwork receiveDataViaNetwork) throws IOException {
        // Crear un objeto Patient y obtener los datos del paciente
        try {
            sendDataViaNetwork.sendInt(2); // Indicar al servidor que se va a registrar un doctor

            Doctor doctor = new Doctor();
            Role role = new Role("Doctor");

            String name = Utilities.readString("Enter your name: ");
            doctor.setName(name);

            String surname = Utilities.readString("Enter your surname: ");
            doctor.setSurname(surname);

            String dni = Utilities.readString("Enter your dni: ");
            doctor.setDNI(dni);

            String dob = Utilities.readString("Enter your date of birth (YYYY-MM-DD): ");
            Date dateOfBirth = Date.valueOf(dob);
            doctor.setBirthDate(dateOfBirth);

            String sex = Utilities.readString("Enter your sex (M/F): ");
            doctor.setSex(sex);

            String email = Utilities.readString("Enter your email: ");
            doctor.setEmail(email);

            String password = Utilities.readString("Enter your password: ");
            byte[] passwordBytes = password.getBytes(); // Convertir la contraseña a bytes

            if (passwordBytes != null) {
                sendDataViaNetwork.sendStrings("OK");
                User user = new User(email, passwordBytes, role);
                System.out.println(doctor);
                System.out.println(user);
                sendDataViaNetwork.sendDoctor(doctor);
                sendDataViaNetwork.sendUser(user);

                if (receiveDataViaNetwork.receiveString().equals("SUCCESS")) {
                    System.out.println("Doctor registered successfully.");

                    DoctorApp.selectPatient(socket, receiveDataViaNetwork, sendDataViaNetwork);
                } else {
                    System.out.println("Registration failed. Please try again.");
                    return; // Salir del metodo si el registro falla
                }
            } else {
                sendDataViaNetwork.sendStrings("ERROR");
            }

        } catch (IOException e) {
            System.out.println("Error in connection");
            releaseResources(socket, sendDataViaNetwork, receiveDataViaNetwork);
            System.exit(0);
        }
    }

    public void logIn(Socket socket, SendDataViaNetwork sendDataViaNetwork, ReceiveDataViaNetwork receiveDataViaNetwork) throws IOException {
        try {
            sendDataViaNetwork.sendInt(1);
            System.out.println(receiveDataViaNetwork.receiveString());

            String username = Utilities.readString("Enter your username: ");

            String password = Utilities.readString("Enter your password: ");

            byte[] passwordBytes = password.getBytes();

            Role role = new Role("Doctor");

            if(passwordBytes != null) {
                sendDataViaNetwork.sendStrings("OK");
                User user = new User(username, passwordBytes, role);
                sendDataViaNetwork.sendUser(user);
                String response = receiveDataViaNetwork.receiveString();
                System.out.println(response);

                if(response.equals("SUCCESS")) {
                    try{
                        Doctor doctor = receiveDataViaNetwork.receiveDoctor();
                        System.out.println(doctor.toString());
                        if (doctor != null) {
                            System.out.println("Log in successful");
                            DoctorApp.selectPatient(socket, receiveDataViaNetwork, sendDataViaNetwork);
                        } else {
                            System.out.println("Doctor not found");
                        }
                    } catch (IOException e) {
                        System.out.println("Log in problem");
                    }
                } else if (response.equals("ERROR")) {
                    System.out.println("User or password is incorrect");
                } else {
                    System.out.println("Login failed. Please check your credentials.");
                }


            }else {
                sendDataViaNetwork.sendStrings("ERROR");
            }

        }catch(IOException e){
            System.out.println("Error in connection");
            releaseResources(socket, sendDataViaNetwork,receiveDataViaNetwork);
            System.exit(0);
        }
    }

    public void viewPatientData(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        // Enviar Id
        sendDataViaNetwork.sendInt(patientId);
        // Recibir los detalles del paciente desde el servidor
        String patient = receiveDataViaNetwork.receiveString();
        System.out.println("Showing patient data:"+patient +"\n");

    }
//    public void viewPatientMedInfo(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
//        //Enviar id
//        sendDataViaNetwork.sendInt(patientId);
//        // Recibir la medical information del paciente desde el servidor
//        int size  = receiveDataViaNetwork.receiveInt();
//        System.out.println("Showing patient med info:\n ");
//        for(int i = 0; i < size; i++) {
//            System.out.println("------------\n");
//            System.out.println(receiveDataViaNetwork.receiveInt());
//            System.out.println(receiveDataViaNetwork.receiveString() + "\n");
//            int medSize = receiveDataViaNetwork.receiveInt();
//            for(int j = 0; j < medSize; j++) {
//                System.out.println(receiveDataViaNetwork.receiveString());
//            }
//            int symptomSize = receiveDataViaNetwork.receiveInt();
//            for(int j = 0; j < symptomSize; j++) {
//                System.out.println(receiveDataViaNetwork.receiveString());
//            }
//        }
//
//    }


    public void viewPatientMedInfo(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        sendDataViaNetwork.sendInt(patientId);
        List<MedicalInformation> medicalInformationList = receiveDataViaNetwork.receiveMedicalInformationList();
        System.out.println("Showing medical information:");
        System.out.println(medicalInformationList);
    }
    public void selectAndUpdateFeedback(int patientid,Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {

        Scanner scanner = new Scanner(System.in);
        sendDataViaNetwork.sendInt(patientid);  // Enviar el patientId al servidor

        // Paso 2: Recibir la lista de registros de medical_information para el paciente
        String recordsResponse = receiveDataViaNetwork.receiveString();  // Recibir la lista de registros

        // Si no hay registros, mostrar un mensaje al médico
        if (recordsResponse.equals("No medical records found for this patient.")) {
            System.out.println(recordsResponse);
            return;
        }

        // Mostrar los registros disponibles al médico
        System.out.println(recordsResponse);  // Ejemplo de respuesta: "1. Date: 2025-11-20 Feedback: No issues."

        // Paso 3: El médico selecciona el registro que quiere actualizar
        System.out.print("Enter the number of the record you want to update: ");
        int selectedIndex = scanner.nextInt();  // Leer el índice del registro seleccionado

        // Validar la selección
        if (selectedIndex < 1 || selectedIndex > recordsResponse.split("\n").length) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }

        // Paso 4: Solicitar el nuevo feedback del médico
        System.out.print("Enter the new feedback: ");
        scanner.nextLine();  // Consumir la nueva línea
        String newFeedback = scanner.nextLine();  // Leer el nuevo feedback

        // Paso 5: Enviar el comando UPDATE_FEEDBACK al servidor, junto con el índice y el nuevo feedback
        sendDataViaNetwork.sendStrings("UPDATE_FEEDBACK");
        sendDataViaNetwork.sendInt(selectedIndex);  // Enviar el ID del registro seleccionado
        sendDataViaNetwork.sendStrings(newFeedback);  // Enviar el nuevo feedback

        // Paso 6: Recibir la respuesta del servidor
        String response = receiveDataViaNetwork.receiveString();
        System.out.println("Feedback response from server: " + response);  // Mostrar la respuesta del servidor
    }

    public void addFeedback(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        sendDataViaNetwork.sendInt(patientId);
        System.out.println("Select a Medical Report:");
        //vemos el tamaño de la lista de medicamentos para ior imprimiendola con un bucle
        int size = receiveDataViaNetwork.receiveInt();
        for(int i = 0; i < size; i++) {
            System.out.println("------------\n");
            System.out.println(receiveDataViaNetwork.receiveInt());
            System.out.println(receiveDataViaNetwork.receiveString() + "\n");
        }

        System.out.println("SELECT BY TYPING THE ID");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        sendDataViaNetwork.sendInt(id);
        System.out.println("Write the Feedback");
        String feedback = scanner.nextLine();
        sendDataViaNetwork.sendStrings(feedback);
    }


    public void viewRecordedSignal(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        // Enviar solicitud para ver las señales grabadas del paciente
        sendDataViaNetwork.sendInt(patientId);  // Enviar el ID del paciente

        // Recibir la señal grabada desde el servidor
        Signal signal = receiveDataViaNetwork.receiveSignal();  // Llamar al método para recibir el Signal

        // Mostrar los detalles de la señal recibida
        System.out.println("Signal Filename: " + signal.getSignalFilename());
        System.out.println("Signal Type: " + signal.getSignalType());
        System.out.println("Signal Values: " + signal.getValues());
    }

    public void changePatientData(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
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
            sendDataViaNetwork.sendStrings(newName);  // Solo enviar el nuevo nombre si fue modificado
        } else {
            sendDataViaNetwork.sendStrings("");  // Enviar una cadena vacía si no se modificó
        }

        if (newSurname != null) {
            sendDataViaNetwork.sendStrings(newSurname);  // Solo enviar el nuevo apellido si fue modificado
        } else {
            sendDataViaNetwork.sendStrings("");  // Enviar una cadena vacía si no se modificó
        }

        if (newPhone != null) {
            sendDataViaNetwork.sendInt(Integer.parseInt(newPhone));  // Solo enviar el nuevo teléfono si fue modificado
        } else {
            sendDataViaNetwork.sendStrings("");  // Enviar una cadena vacía si no se modificó
        }

        if (newEmail != null) {
            sendDataViaNetwork.sendStrings(newEmail);  // Solo enviar el nuevo email si fue modificado
        } else {
            sendDataViaNetwork.sendStrings("");  // Enviar una cadena vacía si no se modificó
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


    private static void releaseResources(Socket socket, SendDataViaNetwork sendDataViaNetwork, ReceiveDataViaNetwork receiveDataViaNetwork) {
        if (sendDataViaNetwork != null && receiveDataViaNetwork != null) {
            sendDataViaNetwork.releaseResources();
            receiveDataViaNetwork.releaseResources();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(DoctorApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

