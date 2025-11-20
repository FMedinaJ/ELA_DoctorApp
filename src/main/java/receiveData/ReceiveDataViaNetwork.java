package receiveData;
import pojos.Patient;
import pojos.Signal;
import pojos.SignalType;
import pojos. *;
import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static java.util.Date.*;

public class ReceiveDataViaNetwork {

    private DataInputStream dataInputStream;
    private Socket socket;

    public ReceiveDataViaNetwork(Socket socket) {
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicializing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String receiveString() throws IOException {
        return dataInputStream.readUTF();
    }

    public Doctor receiveDoctor() {
        Doctor doctor = null;
        try {
            int id = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String DNI = dataInputStream.readUTF();
            java.sql.Date birthDate = Date.valueOf(dataInputStream.readUTF());
            String sex = dataInputStream.readUTF();
            String email = dataInputStream.readUTF();
            doctor = new Doctor(id, name, surname, DNI, birthDate, sex, email);

        } catch (EOFException ex){
            System.out.println("Data not correctly read.");
    }catch(IOException e){
        System.err.println("Error receiving patient data: " + e.getMessage());
        e.printStackTrace();
    }
        return doctor;
    }

    public Patient receivePatient(){
        Patient patient = null;
        try {
            int id = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String dni = dataInputStream.readUTF();
            Date birthDate = Date.valueOf(dataInputStream.readUTF());
            String sex = dataInputStream.readUTF();
            Integer phone = dataInputStream.readInt();
            String email = dataInputStream.readUTF();
            Integer insurance = Integer.valueOf(dataInputStream.readUTF());
            patient = new Patient(id, name, surname, dni, birthDate, sex, phone, email, insurance);
        } catch (EOFException ex) {
            System.out.println("Data not correctly read.");
        } catch (IOException ex) {
            System.err.println("Error receiving patient data: " + ex.getMessage());
            ex.printStackTrace();
        }
        return patient;
    }
    //Obtiene el sintoma desde el servidor, se solicita la informacion.
    public Symptom getSymptomFromServer(int symptomId) {
        try {
            // Aquí podrías enviar el ID del síntoma al servidor y esperar la respuesta
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(symptomId);  // Enviando solicitud de síntoma por ID

            // Esperando la respuesta (el objeto Symptom) del servidor
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (Symptom) objectInputStream.readObject();  // Recibiendo el objeto Symptom
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error fetching symptom from server: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
}
    public MedicalInformation receiveMedicalInformation() {
        MedicalInformation medicalInformation = null;
        try {
            int id = dataInputStream.readInt();  // Recibe el ID de la información médica
            Date reportDate = Date.valueOf(dataInputStream.readUTF());  // Recibe la fecha del informe

            int symptomsCount = dataInputStream.readInt();  // Número de síntomas
            List<Symptom> symptoms = new ArrayList<>();
            for (int i = 0; i < symptomsCount; i++) {
                int symptomId = dataInputStream.readInt();  // ID del síntoma
                // Solicitar el síntoma al servidor
                Symptom symptom = getSymptomFromServer(symptomId);  // Obtener el síntoma desde el servidor
                if (symptom != null) {
                    symptoms.add(symptom);
                }
            }

            // Recibe la lista de medicamentos
            int medicationCount = dataInputStream.readInt();
            List<String> medication = new ArrayList<>();
            for (int i = 0; i < medicationCount; i++) {
                medication.add(dataInputStream.readUTF());  // Agrega cada medicamento a la lista
            }

            // Recibe el feedback
            String feedback = dataInputStream.readUTF();  // Retroalimentación

            // Crea la instancia de MedicalInformation con todos los datos
            medicalInformation = new MedicalInformation(id,symptoms, reportDate, medication, feedback);

        } catch (IOException ex) {
            System.err.println("Error receiving medical information: " + ex.getMessage());
            ex.printStackTrace();
        }
        return medicalInformation;
    }
    public int receiveInt() {
        int message = 0;
        try {
            message = dataInputStream.readInt();
        } catch (IOException ex) {
            System.err.println("Error receiving int: " + ex.getMessage());
            ex.printStackTrace();
        }
        return message;
    }
    public List<Patient> receivePatientList() {
        List<Patient> patients = new ArrayList<>();
        try {
            int numberOfPatients = dataInputStream.readInt();  // Primero, leer cuántos pacientes hay
                for (int i = 0; i < numberOfPatients; i++) {
                    // Aquí estamos utilizando el método `receivePatient` para recibir a cada paciente individualmente
                    Patient patient = receivePatient();
                    if (patient != null) {
                        patients.add(patient);
                    }
            }
        } catch (IOException e) {
            System.err.println("Error receiving patient list: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;  // Regresamos la lista de pacientes
    }

    // Método para recibir un Signal
    public Signal receiveSignal() throws IOException {
        // Recibir la longitud de la lista de valores
        int size = dataInputStream.readInt();  // Primero leemos el tamaño de la lista

        // Crear una lista de enteros (List<Integer>)
        List<Integer> values = new ArrayList<>();

        // Leer cada valor de la lista (List<Integer>)
        for (int i = 0; i < size; i++) {
            values.add(dataInputStream.readInt());  // Recibir y agregar cada valor a la lista
        }

        // Recibir el nombre del archivo (String) - signalFilename
        String signalFilename = dataInputStream.readUTF();

        // Recibir el tipo de señal (SignalType) como String y convertirlo a SignalType
        String signalTypeString = dataInputStream.readUTF();  // Recibimos el tipo de la señal como String
        SignalType signalType = SignalType.valueOf(signalTypeString);  // Convertimos el String a enum SignalType

        // Crear y devolver el objeto Signal con los datos recibidos
        return new Signal(values, signalFilename, signalType);
    }
    public void releaseResources() {
        try {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        } catch (IOException ex) {
            System.err.println("Error with resources: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}

