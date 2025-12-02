package receiveData;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pojos.*;



public class SendDataViaNetwork {
    private DataOutputStream dataOutputStream;

    public SendDataViaNetwork(Socket socket) {
        try {
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Error al inicializar el flujo de salida: " + ex.getMessage());
            Logger.getLogger(SendDataViaNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendStrings(String message) throws IOException {
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }

    public void sendInt(int message) throws IOException {
        dataOutputStream.writeInt(message);
        dataOutputStream.flush();
    }

    public void sendUser(User user) throws IOException{
        dataOutputStream.writeUTF(user.getEmail());
        dataOutputStream.writeUTF(String.valueOf(user.getRole()));
        byte[] password = user.getPasswordEncripted();
        dataOutputStream.writeUTF(new String(password));
    }
    public void sendMedicalInformation(MedicalInformation medicalInformation) throws IOException {
        // Enviar el ID de la información médica
        dataOutputStream.writeInt(medicalInformation.getId());

        // Enviar la fecha del informe
        dataOutputStream.writeUTF(String.valueOf(medicalInformation.getReportDate()));

        // Enviar la lista de síntomas
        List<Symptom> symptoms = medicalInformation.getSymptoms();
        dataOutputStream.writeInt(symptoms.size());  // Enviar la cantidad de síntomas
        for (Symptom symptom : symptoms) {
            dataOutputStream.writeInt(symptom.getId());  // Enviar el ID del síntoma
        }

        // Enviar la lista de medicamentos
        List<String> medication = medicalInformation.getMedication();
        dataOutputStream.writeInt(medication.size());  // Enviar la cantidad de medicamentos
        for (String med : medication) {
            dataOutputStream.writeUTF(med);  // Enviar cada medicamento
        }

        // Enviar el feedback
        dataOutputStream.writeUTF(medicalInformation.getFeedback());

        // Asegurarse de que los datos se escriban completamente
        dataOutputStream.flush();
    }

    public void sendMedicalInformationList(List<MedicalInformation> medicalInformation) throws IOException {
        dataOutputStream.writeInt(medicalInformation.size());
        for (MedicalInformation mi : medicalInformation) {
            dataOutputStream.writeInt(mi.getId());
            dataOutputStream.writeUTF(mi.getReportDate().toString());
            dataOutputStream.writeInt(mi.getMedication().size());
            for (String med : mi.getMedication()){
                dataOutputStream.writeUTF(med);
            }
            dataOutputStream.writeInt(mi.getSymptoms().size());
            for (Symptom s : mi.getSymptoms()) {
                dataOutputStream.writeInt(s.getId());
                dataOutputStream.writeUTF(s.getDescription());
            }
            dataOutputStream.writeUTF(mi.getFeedback());
        }
        // Asegurarse de que los datos se escriban completamente
        dataOutputStream.flush();
    }

    public void sendPatient(Patient patient) throws IOException{
        dataOutputStream.writeUTF(patient.getName());
        dataOutputStream.writeUTF(patient.getSurname());
        dataOutputStream.writeUTF(patient.getDni());
        dataOutputStream.writeUTF(String.valueOf(patient.getDateOfBirth()));
        dataOutputStream.writeUTF(patient.getSex());
        dataOutputStream.writeInt(patient.getPhone());
        dataOutputStream.writeUTF(patient.getEmail());
        dataOutputStream.writeInt(patient.getInsurance());
        dataOutputStream.flush();
    }

    public void sendDoctor(Doctor doctor) throws IOException {
        //dataOutputStream.writeInt(doctor.getId());
        dataOutputStream.writeUTF(doctor.getName());
        dataOutputStream.writeUTF(doctor.getSurname());
        dataOutputStream.writeUTF(doctor.getDNI());
        dataOutputStream.writeUTF(String.valueOf(doctor.getBirthDate()));
        dataOutputStream.writeUTF(doctor.getSex());
        dataOutputStream.writeUTF(doctor.getEmail());
        dataOutputStream.flush();


    }

    public void sendSignal(Signal signal) throws IOException {
        //Enviar la longitud de la lista de valores
        dataOutputStream.writeInt(signal.getValues().size());  // Enviar el tamaño de la lista

        // Enviar cada valor de la lista de Integer
        for (Integer value : signal.getValues()) {
            dataOutputStream.writeInt(value);  // Enviar cada valor
        }

        // Enviar el nombre del archivo (signalFilename)
        dataOutputStream.writeUTF(signal.getSignalFilename());

        // Enviar el tipo de señal (signalType) como String
        dataOutputStream.writeUTF(signal.getType().toString());  // Convertir a String y enviar
    }

    public void releaseResources() {
        try {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        } catch (IOException ex) {
            System.err.println("Error with resources: " + ex.getMessage());
            Logger.getLogger(SendDataViaNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}





