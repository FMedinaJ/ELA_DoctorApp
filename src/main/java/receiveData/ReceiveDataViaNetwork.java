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
            System.out.println("Receiving patient data...");
            int id = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String dni = dataInputStream.readUTF();
            Date birthDate = Date.valueOf(dataInputStream.readUTF());
            String sex = dataInputStream.readUTF();
            int phone = dataInputStream.readInt();
            String email = dataInputStream.readUTF();
            int insurance = dataInputStream.readInt();
            patient = new Patient(id,name, surname, dni, birthDate, sex, phone, email, insurance);
            System.out.println(patient);
            return patient;
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
            int id = dataInputStream.readInt();

            Date reportDate = Date.valueOf(dataInputStream.readUTF());  // Recibe la fecha del informe

            List<Symptom> symptoms = receiveSymptoms();

            List<String> medication = receiveMedications();

            String feedback = dataInputStream.readUTF();

            // Crea la instancia de MedicalInformation con todos los datos
            medicalInformation = new MedicalInformation(id, symptoms, reportDate, medication, feedback);

        } catch (IOException ex) {
            System.err.println("Error receiving medical information: " + ex.getMessage());
            ex.printStackTrace();
        }
        return medicalInformation;
    }

    public List<MedicalInformation> receiveMedicalInformationList() throws IOException {

        int size = dataInputStream.readInt();
        List<MedicalInformation> medicalInformationList = new ArrayList<>();

        if(size == 0) {
            return medicalInformationList;
        }else{
            for (int i = 0; i < size; i++) {
                MedicalInformation medicalInformation = receiveMedicalInformation();
                medicalInformationList.add(medicalInformation);
            }
            System.out.println("Received medical information, size: " + medicalInformationList.size());
        }

        return medicalInformationList;
    }

    public List<Symptom> receiveSymptoms() throws IOException {
        // 1. Leer cuántos síntomas vienen
        int size = dataInputStream.readInt();

        List<Symptom> symptoms = new ArrayList<Symptom>();

        // 2. Leer cada síntoma en el mismo orden en el que se envió
        for (int i = 0; i < size; i++) {
            int id = dataInputStream.readInt();
            String description = dataInputStream.readUTF();

            // Ajusta esto al constructor/setters que tengas en tu clase Symptom
            Symptom symptom = new Symptom(id,description);
            symptoms.add(symptom);
        }

        return symptoms;
    }

    public List<String> receiveMedications() throws IOException {
        // 1. Leer cuántos síntomas vienen
        int size = dataInputStream.readInt();

        List<String> medications = new ArrayList<String>();

        // 2. Leer cada síntoma en el mismo orden en el que se envió
        for (int i = 0; i < size; i++) {
            String medication = dataInputStream.readUTF();
            medications.add(medication);
        }

        return medications;
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

    // En receiveData.ReceiveDataViaNetwork.java

    public Signal receiveSignal() throws IOException {
        // 1. Recibir TAMAÑO de la lista de valores
        int size = dataInputStream.readInt();

        // 2. Recibir VALORES (Muestras)
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            values.add(dataInputStream.readInt());
        }

        // 3. Recibir NOMBRE DE ARCHIVO
        String signalFilename = dataInputStream.readUTF();

        // 4. Recibir TIPO de señal
        String signalTypeString = dataInputStream.readUTF();
        TypeSignal signalType;
        try {
            signalType = TypeSignal.valueOf(signalTypeString);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown signal type received: " + signalTypeString);
            signalType = TypeSignal.EMG; // Default fallback
        }

        String dateString = dataInputStream.readUTF();
        Date date = null;
        try {
            date = Date.valueOf(dateString); // Convertir String "YYYY-MM-DD" a Date
        } catch (IllegalArgumentException e) {
            // Si es "Unknown Date" o formato inválido, dejamos null o fecha actual
            date = new Date(System.currentTimeMillis());
        }
        // Crear el objeto Signal con el constructor adecuado
        // Usamos el constructor que creaste: Signal(List<Integer> values, String signalFilename, TypeSignal type)
        Signal signal = new Signal(values, signalFilename, signalType);
        signal.setDate(date); // <--- AÑADIR LA FECHA AL OBJETO

        return signal;
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

