package UI;

import pojos.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import receiveData. *;

import javax.imageio.ImageIO;
import javax.swing.*;

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
        // Recibir los detalles del paciente desde el servidor

        Patient patient = receiveDataViaNetwork.receivePatient();
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

    public void showMedicalInformation(Socket socket, SendDataViaNetwork sendDataViaNetwork, ReceiveDataViaNetwork receiveDataViaNetwork) throws IOException {
        // Solicitar al servidor la información médica del paciente
        // Recibir la lista de la información médica
        List<MedicalInformation> medicalInfos = receiveDataViaNetwork.receiveMedicalInformationList();

        if(medicalInfos != null) {
            String response = "RECEIVED MEDICAL INFORMATION";
            sendDataViaNetwork.sendStrings(response);
            // Mostrar la información médica al doctor
            for (MedicalInformation info : medicalInfos) {
                System.out.println(info);  // Mostrar los detalles de cada informe
            }
        }else{
            System.out.println("Medical information not found");
        }


    }
    public void selectAndUpdateFeedback(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        Scanner scanner = new Scanner(System.in);
        sendDataViaNetwork.sendInt(patientId);  // Enviar el patientId al servidor

        // Paso 2: Recibir la lista de registros médicos del paciente
        String recordsResponse = receiveDataViaNetwork.receiveString();  // Recibir los registros

        // Si no hay registros, mostrar un mensaje y salir
        if (recordsResponse.equals("No medical records found for this patient.")) {
            System.out.println(recordsResponse);  // Mostrar el mensaje al doctor
            return;  // Terminar la ejecución si no hay registros médicos
        }

        // Paso 3: Mostrar los registros disponibles al médico
        System.out.println(recordsResponse);  // Ejemplo de respuesta: "1. Date: 2025-11-20 Feedback: No issues."

        // Paso 4: Solicitar al médico que seleccione el registro que desea actualizar
        System.out.print("Enter the number of the record you want to update: ");
        int selectedIndex = scanner.nextInt();  // Leer el índice del registro seleccionado

        // Validar la selección
        if (selectedIndex < 1 || selectedIndex > recordsResponse.split("\n").length) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }

        // Paso 5: Solicitar el nuevo feedback del médico
        System.out.print("Enter the new feedback: ");
        scanner.nextLine();  // Consumir la nueva línea
        String newFeedback = scanner.nextLine();  // Leer el nuevo feedback

        // Paso 6: Enviar el comando UPDATE_FEEDBACK al servidor, junto con el índice y el nuevo feedback
        sendDataViaNetwork.sendStrings("UPDATE_FEEDBACK");
        sendDataViaNetwork.sendInt(selectedIndex);  // Enviar el ID del registro seleccionado
        sendDataViaNetwork.sendStrings(newFeedback);  // Enviar el nuevo feedback

        // Paso 7: Recibir la respuesta del servidor
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

    public void changePatientData(int patientId, Socket socket, ReceiveDataViaNetwork receiveDataViaNetwork, SendDataViaNetwork sendDataViaNetwork) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Mostrar las opciones al doctor
        System.out.println("What information would you like to change?");
        System.out.println("1 - Name");
        System.out.println("2 - Surname");
        System.out.println("3 - Phone");
        System.out.println("4 - Email");
        System.out.println("5 - DNI");
        System.out.println("6 - Sex");
        System.out.println("7- Insurance");
        System.out.println("0 - Exit");

        // Leer la opción seleccionada por el doctor
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consumir la nueva línea

        // Variables para los nuevos valores
        String newName = null, newSurname = null,  newEmail = null;
        String newdni= null, newSex=null;
        Integer newInsurance = null, newPhone= null; // Usamos Integer para los campos int en caso de que no se ingrese un valor

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
                String newPhoneStr = scanner.nextLine();
                // Verificar que el seguro sea un número válido
                try {
                    newPhone = Integer.parseInt(newPhoneStr);  // Convertir a int
                } catch (NumberFormatException e) {
                    System.out.println("Invalid insurance number. Please enter a valid number.");
                    return;  // Salir del método si el número no es válido
                }
                break;
            case 4:
                // Solicitar nuevo email
                System.out.print("Enter new email: ");
                newEmail = scanner.nextLine();
                break;
            case 5:
                System.out.print("Enter new DNI: ");
                newdni= scanner.nextLine();
                break;
            case 6:
                System.out.print("Enter new Sex: ");
                newSex= scanner.nextLine();
                break;
            case 7:
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
            sendDataViaNetwork.sendInt(newPhone);  // Solo enviar el nuevo teléfono si fue modificado
        } else {
            sendDataViaNetwork.sendInt(-1);  // Enviar una cadena vacía si no se modificó
        }

        if (newEmail != null) {
            sendDataViaNetwork.sendStrings(newEmail);  // Solo enviar el nuevo email si fue modificado
        } else {
            sendDataViaNetwork.sendStrings("");  // Enviar una cadena vacía si no se modificó
        }
        if(newdni != null) {
            sendDataViaNetwork.sendStrings(newdni);
        }else{
            sendDataViaNetwork.sendStrings("");
        }
        if(newSex != null) {
            sendDataViaNetwork.sendStrings(newSex);
        }else{
            sendDataViaNetwork.sendStrings("");
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
    public boolean registerFromGUI(
            String name,
            String surname,
            String dni,
            String dob,
            String sex,
            String email,
            String password,
            Socket socket,
            SendDataViaNetwork sendDataViaNetwork,
            ReceiveDataViaNetwork receiveDataViaNetwork) throws IOException {

        sendDataViaNetwork.sendInt(2); // registrar doctor

        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSurname(surname);
        doctor.setDNI(dni);
        doctor.setBirthDate(Date.valueOf(dob));
        doctor.setSex(sex);
        doctor.setEmail(email);

        byte[] passwordBytes = password.getBytes();
        Role role = new Role("Doctor");
        User user = new User(email, passwordBytes, role);

        sendDataViaNetwork.sendStrings("OK");
        sendDataViaNetwork.sendDoctor(doctor);
        sendDataViaNetwork.sendUser(user);

        String response = receiveDataViaNetwork.receiveString(); // "SUCCESS" o "ERROR"
        return response.equals("SUCCESS");
    }
    public boolean logInFromGUI(
            String username,
            String password,
            Socket socket,
            SendDataViaNetwork sendDataViaNetwork,
            ReceiveDataViaNetwork receiveDataViaNetwork) throws IOException {

        sendDataViaNetwork.sendInt(1); // login

        // mensaje inicial del servidor, lo leemos y lo ignoramos o mostramos en consola
        String serverMsg = receiveDataViaNetwork.receiveString();
        System.out.println("Server says: " + serverMsg);

        byte[] passwordBytes = password.getBytes();
        Role role = new Role("Doctor");
        User user = new User(username, passwordBytes, role);

        sendDataViaNetwork.sendStrings("OK");
        sendDataViaNetwork.sendUser(user);

        String response = receiveDataViaNetwork.receiveString(); // "SUCCESS" o "ERROR"
        if (!response.equals("SUCCESS")) {
            return false;
        }

        Doctor doctor = receiveDataViaNetwork.receiveDoctor();
        System.out.println("Doctor logged in: " + doctor);
        return doctor != null;
    }

    public String viewPatientDetailsAndMedicalInfoFromGUI(
            int patientId,
            Socket socket,
            ReceiveDataViaNetwork receiveDataViaNetwork,
            SendDataViaNetwork sendDataViaNetwork) throws IOException {

        // 1) Código de operación (ajusta si tu servidor usa otro)
        sendDataViaNetwork.sendInt(1);

        // 2) Enviar ID del paciente
        sendDataViaNetwork.sendInt(patientId);

        // 3) Recibir objeto Patient
        Patient patient = receiveDataViaNetwork.receivePatient();

        // 4) Recibir lista de informes médicos
        List<MedicalInformation> medicalInfos = receiveDataViaNetwork.receiveMedicalInformationList();

        if (medicalInfos != null) {
            sendDataViaNetwork.sendStrings("RECEIVED MEDICAL INFORMATION");
        }

        // ========================
        // ----- FORMATO BONITO ---
        // ========================

        StringBuilder sb = new StringBuilder();

        sb.append("==== PATIENT DETAILS ====\n");

        sb.append("ID: ").append(patient.getId()).append("\n");
        sb.append("Name: ").append(patient.getName()).append(" ").append(patient.getSurname()).append("\n");
        sb.append("DNI: ").append(patient.getDni()).append("\n");
        sb.append("Birth date: ").append(patient.getDateOfBirth()).append("\n");
        sb.append("Sex: ").append(patient.getSex()).append("\n");
        sb.append("Phone: ").append(patient.getPhone()).append("\n");
        sb.append("Email: ").append(patient.getEmail()).append("\n");
        sb.append("Insurance: ").append(patient.getInsurance()).append("\n");

        sb.append("\n==== MEDICAL INFORMATION ====\n");

        if (medicalInfos == null || medicalInfos.isEmpty()) {
            sb.append("No medical information found\n");
        } else {
            int index = 1;
            for (MedicalInformation info : medicalInfos) {
                sb.append("\n--- Report #").append(index++).append(" ---\n");
                sb.append("Date: ").append(info.getReportDate()).append("\n");

                // ====== FORMATEO BONITO DE LOS SÍNTOMAS ======
                // Suponiendo: info.getSymptoms() devuelve List<Symptoms>
                List<Symptom> symptomsList = info.getSymptoms();

                if (symptomsList == null || symptomsList.isEmpty()) {
                    sb.append("Symptoms: none\n");
                } else {
                    sb.append("Symptoms:\n");
                    for (Symptom s : symptomsList) {
                        sb.append(" - ").append(s.getDescription()).append("\n");
                    }
                }
                // =============================================

                sb.append("Doctor feedback: ").append(info.getFeedback()).append("\n");
            }
        }

        sb.append("\n==========================\n");

        return sb.toString();
    }



    public String changePatientDataFromGUI(
            int patientId,
            Socket socket,
            ReceiveDataViaNetwork receiveDataViaNetwork,
            SendDataViaNetwork sendDataViaNetwork,
            java.awt.Component parent) throws IOException {

        String[] options = { "Name", "Surname", "Phone", "Email", "DNI", "Sex", "Insurance" };
        String choice = (String) JOptionPane.showInputDialog(
                parent,
                "What information would you like to change?",
                "Change patient data",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) return "Operation cancelled";

        String newName = null, newSurname = null, newEmail = null, newDni = null, newSex = null;
        Integer newPhone = null, newInsurance = null;

        switch (choice) {
            case "Name":
                newName = JOptionPane.showInputDialog(parent, "Enter new name:");
                if (newName == null) return "Operation cancelled";
                break;
            case "Surname":
                newSurname = JOptionPane.showInputDialog(parent, "Enter new surname:");
                if (newSurname == null) return "Operation cancelled";
                break;
            case "Phone":
                String phoneStr = JOptionPane.showInputDialog(parent, "Enter new phone:");
                if (phoneStr == null) return "Operation cancelled";
                try {
                    newPhone = Integer.parseInt(phoneStr);
                } catch (NumberFormatException e) {
                    return "Invalid phone number.";
                }
                break;
            case "Email":
                newEmail = JOptionPane.showInputDialog(parent, "Enter new email:");
                if (newEmail == null) return "Operation cancelled";
                break;
            case "DNI":
                newDni = JOptionPane.showInputDialog(parent, "Enter new DNI:");
                if (newDni == null) return "Operation cancelled";
                break;
            case "Sex":
                newSex = JOptionPane.showInputDialog(parent, "Enter new sex:");
                if (newSex == null) return "Operation cancelled";
                break;
            case "Insurance":
                String insuranceStr = JOptionPane.showInputDialog(parent, "Enter new insurance:");
                if (insuranceStr == null) return "Operation cancelled";
                try {
                    newInsurance = Integer.parseInt(insuranceStr);
                } catch (NumberFormatException e) {
                    return "Invalid insurance number.";
                }
                break;
        }

        // opción 4 en el menú
        sendDataViaNetwork.sendInt(4);
        sendDataViaNetwork.sendInt(patientId);

        if (newName != null) sendDataViaNetwork.sendStrings(newName); else sendDataViaNetwork.sendStrings("");
        if (newSurname != null) sendDataViaNetwork.sendStrings(newSurname); else sendDataViaNetwork.sendStrings("");
        if (newPhone != null) sendDataViaNetwork.sendInt(newPhone); else sendDataViaNetwork.sendInt(-1);
        if (newEmail != null) sendDataViaNetwork.sendStrings(newEmail); else sendDataViaNetwork.sendStrings("");
        if (newDni != null) sendDataViaNetwork.sendStrings(newDni); else sendDataViaNetwork.sendStrings("");
        if (newSex != null) sendDataViaNetwork.sendStrings(newSex); else sendDataViaNetwork.sendStrings("");
        if (newInsurance != null) sendDataViaNetwork.sendInt(newInsurance); else sendDataViaNetwork.sendInt(-1);

        String response = receiveDataViaNetwork.receiveString();
        return response;
    }

    public void viewRecordedSignal(int patientId, Socket socket, ReceiveDataViaNetwork receiveData, SendDataViaNetwork sendData) throws IOException {
        // 1. Solicitamos al servidor las señales de este paciente
        sendData.sendInt(patientId);

        // 2. Recibimos la cantidad de señales disponibles
        int signalCount = receiveData.receiveInt();

        if (signalCount == 0) {
            System.out.println("No recorded signals found for this patient.");
            return;
        }

        // 3. Recibimos la lista de metadatos y la mostramos
        System.out.println("--- AVAILABLE SIGNALS ---");
        List<Integer> signalIds = new ArrayList<>();

        for (int i = 0; i < signalCount; i++) {
            int signalId = receiveData.receiveInt(); // ID de BBDD
            String type = receiveData.receiveString();
            String date = receiveData.receiveString();

            signalIds.add(signalId);
            System.out.println((i + 1) + ". Type: " + type + " | Date: " + date);
        }

        // 4. El doctor selecciona una
        int selection = Utilities.readInteger("Select a signal number to view (0 to cancel): ");

        if (selection < 1 || selection > signalCount) {
            System.out.println("Operation cancelled.");
            sendData.sendInt(-1); // Enviamos -1 para indicar cancelación al servidor
            return;
        }

        // 5. Enviamos el ID de la señal seleccionada (el ID de base de datos, no el índice del menú)
        int selectedSignalId = signalIds.get(selection - 1);
        sendData.sendInt(selectedSignalId);

        // 6. Recibimos la señal completa con sus valores
        System.out.println("Downloading signal data...");
        Signal signal = receiveData.receiveSignal(); // Este método ya lo tienes en tu clase ReceiveData

        if (signal != null && !signal.getValues().isEmpty()) {
            System.out.println("Signal received! (" + signal.getValues().size() + " samples)");

            // 7. Generar y abrir la gráfica
            generateAndOpenGraph(signal);
        } else {
            System.out.println("Error receiving signal or signal is empty.");
        }
    }

    // --- MÉTODO AUXILIAR PARA GENERAR LA IMAGEN EN EL ORDENADOR DEL DOCTOR ---
    private void generateAndOpenGraph(Signal signal) {
        int width = 1000;
        int height = 600;
        int padding = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        // Fondo blanco
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // Ejes
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, height - padding, width - padding, height - padding);
        g2.drawLine(padding, padding, padding, height - padding);

        // Datos
        List<Integer> values = signal.getValues();
        double xScale = (double) (width - 2 * padding) / (values.size() - 1);
        double yScale = (double) (height - 2 * padding) / 1023.0; // BITalino 10-bit max

        g2.setColor(Color.BLUE);
        for (int i = 0; i < values.size() - 1; i++) {
            int x1 = padding + (int) (i * xScale);
            int y1 = height - padding - (int) (values.get(i) * yScale);
            int x2 = padding + (int) ((i + 1) * xScale);
            int y2 = height - padding - (int) (values.get(i + 1) * yScale);
            g2.drawLine(x1, y1, x2, y2);
        }

        // Títulos
        g2.setColor(Color.RED);
        g2.drawString("Signal Type: " + signal.getType(), width / 2, 20);
        g2.dispose();

        // Guardar y Abrir
        try {
            // Guardamos la imagen en una carpeta temporal del Doctor
            File outputfile = new File("Doctor_View_" + System.currentTimeMillis() + ".png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Graph generated: " + outputfile.getAbsolutePath());

            // Abrir automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(outputfile);
            }
        } catch (IOException e) {
            System.out.println("Error creating graph image: " + e.getMessage());
        }
    }

}

