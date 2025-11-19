package receiveData;


import sun.misc.Signal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import pojos.*;



public class SendDataViaNetwork {
    private DataOutputStream dataOutputStream;
    public SendDataViaNetwork(Socket socket)  {
        try{
            this.dataOutputStream= new DataOutputStream(socket.getOutputStream());

        }catch(IOException e){
            e.printStackTrace();

        }

    }
    public void sendString(String data){
        try{
            dataOutputStream.writeUTF(data);

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void sendInt(int data){
        try{
            dataOutputStream.writeInt(data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendUser( User user ){
        try{
            dataOutputStream.writeInt(user.getId());
            dataOutputStream.writeUTF(user.getUsername());
            dataOutputStream.writeUTF(user.getPasswordEncripted());
            dataOutputStream.writeUTF(user.getRole().toString());

        }catch(IOException e){
            e.printStackTrace();
        }
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

    public void sendPatient(Patient patient) throws IOException{
        dataOutputStream.writeInt(patient.getId());
        dataOutputStream.writeUTF(patient.getName());
        dataOutputStream.writeUTF(patient.getSurname());
        dataOutputStream.writeUTF(String.valueOf(patient.getInsurance()));
        dataOutputStream.flush();
    }

    public void sendDoctor(Doctor doctor) throws IOException {
        dataOutputStream.writeInt(doctor.getId());
        dataOutputStream.writeUTF(doctor.getName());
        dataOutputStream.writeUTF(doctor.getSurname());
        dataOutputStream.writeUTF(doctor.getDNI());
        dataOutputStream.writeUTF(String.valueOf(doctor.getBirthDate()));
        dataOutputStream.writeUTF(doctor.getSex());
        dataOutputStream.writeUTF(doctor.getEmail());
        dataOutputStream.flush();


    }

    // Método para enviar un Signal
    public void sendSignal(Signal signal) throws IOException {
        /**Enviar la longitud de la lista de valores
        dataOutputStream.writeInt(signal.getValues().size());  // Enviar el tamaño de la lista

        // Enviar cada valor de la lista de Integer
        for (Integer value : signal.getValues()) {
            dataOutputStream.writeInt(value);  // Enviar cada valor
        }

        // Enviar el nombre del archivo (signalFilename)
        dataOutputStream.writeUTF(signal.getSignalFilename());

        // Enviar el tipo de señal (signalType) como String
        dataOutputStream.writeUTF(signal.getSignalType().toString());  // Convertir a String y enviar**/
    }
}





