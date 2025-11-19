package receiveData;
import pojos.*;

import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static java.util.Date.*;

public class ReceiveDataViaNetwork {

    private DataInputStream dataInputStream;
    private Socket socket;

    public ReceiveDataViaNetwork(Socket socket)  {
        try{
            this.dataInputStream= new DataInputStream(socket.getInputStream());

        }catch(IOException e){
            e.printStackTrace();

        }

    }
    public String receiveString(){
        try{
            String information;
            information= dataInputStream.readUTF();
            return information;

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;

    }
    public Doctor receiveDoctor(){
        try{
            Doctor doctor;
            int id = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String DNI = dataInputStream.readUTF();
            java.sql.Date birthDate = Date.valueOf (dataInputStream.readUTF());
            String gender = dataInputStream.readUTF();
            String email = dataInputStream.readUTF();
            doctor= new Doctor(id, name, surname, DNI, birthDate, gender, email);
            return doctor;

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Patient receivePatient(){
        Patient patient= null;
        try{
            int id=dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String DNI = dataInputStream.readUTF();
            java.sql.Date birthDate = Date.valueOf (dataInputStream.readUTF());
            String sex = dataInputStream.readUTF();
            Integer phone = dataInputStream.readInt();
            String email = dataInputStream.readUTF();
            Integer insurance = dataInputStream.readInt();
            patient= new Patient(id,name,surname,DNI,birthDate,sex,phone,email,insurance);
        }catch(IOException e){
            e.printStackTrace();
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


}
