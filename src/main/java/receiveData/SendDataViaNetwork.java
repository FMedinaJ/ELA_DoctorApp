package receiveData;

import pojos.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
            dataOutputStream.writeUTF(user.getId());
        }catch(IOException e){
            e.printStackTrace();
        }
    }






}

