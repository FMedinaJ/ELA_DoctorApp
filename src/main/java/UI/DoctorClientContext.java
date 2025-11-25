package UI;
import receiveData. *;

import java.io.IOException;
import java.net.Socket;

public class DoctorClientContext {
    private Socket socket;
    private SendDataViaNetwork sendData;
    private ReceiveDataViaNetwork receiveData;
    private DoctorUI doctorUI;

    public DoctorClientContext(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.sendData = new SendDataViaNetwork(socket);
        this.receiveData = new ReceiveDataViaNetwork(socket);
        this.doctorUI = new DoctorUI();

        // Identificar al servidor que somos DOCTOR
        sendData.sendInt(2);
        String msg = receiveData.receiveString();

        if (!"DOCTOR".equals(msg)) {
            throw new IOException("Server did not accept doctor client. Response: " + msg);
        }
    }

    public Socket getSocket() { return socket; }
    public SendDataViaNetwork getSendData() { return sendData; }
    public ReceiveDataViaNetwork getReceiveData() { return receiveData; }
    public DoctorUI getDoctorUI() { return doctorUI; }
}
