package receiveData;
import pojos.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Date;

import static java.util.Date.*;

public class ReceiveDataViaNetwork {

    private DataInputStream dataInputStream;

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
        try{
            Patient patient;
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
        return null;
    }
    /*public MedicalInformation receiveMedicalInformation(){
        try{
            MedicalInformation medicalInformation;
            medicalInformation= new MedicalInformation();

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }*/




}
