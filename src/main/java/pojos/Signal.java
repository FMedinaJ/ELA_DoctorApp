package pojos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents a signal associated with a patient.
 * It supports EMG and Accelerometer signals, and provides functionalities for
 * storing, processing, and managing signal data.
 */
public class Signal {
    /**
     * A list of integer values representing the signal data.
     */
    private List<Integer> values;

    /**
     * The filename where the signal data is stored.
     */
    private String signalFilename;

    /**
     * The type of the signal (EMG or Accelerometer).
     */
    private SignalType signalType;

    /**
     * The sampling rate used for the signal data (in Hz).
     * Default value is 100 Hz.
     */
    public static final int samplingrate = 100;

    /**
     * Enum representing the possible types of signals.
     */
    public enum SignalType {
        /**
         * Represents an EMG (electromyography) signal.
         */
        EMG,

        /**
         * Represents an Accelerometer signal.
         */
        Accelerometer
    }

    /**
     * Constructor to initialize a signal with a specific type.
     * @param signaltype the type of the signal (EMG or Accelerometer).
     */
    public Signal(SignalType signaltype){
        this.values = new LinkedList<>();
        this.signalType = signaltype;
    }

    /**
     * Returns the list of values representing the signal data.
     *
     * @return A list of integers representing the signal data.
     */
    public List<Integer> getValues() {
        return values;
    }

    /**
     * Sets the list of values for the signal data.
     *
     * @param values A list of integers representing the signal data to set.
     */
    public void setValues(List<Integer> values) {
        this.values = values;
    }

    /**
     * Sets the EMG (electromyography) signal data by converting a string representation to values.
     *
     * @param stringEMG A string containing the EMG signal data.
     */
    public void setValuesEMG(String stringEMG) {
        this.values = stringToValues(stringEMG);
    }

    /**
     * Sets the Accelerometer signal data by converting a string representation to values.
     *
     * @param stringAccelerometer A string containing the Accelerometer signal data.
     */
    public void setValuesAccelerometer(String stringAccelerometer) {
        this.values = stringToValues(stringAccelerometer);
    }

    /**
     * Returns the filename where the signal data is stored.
     *
     * @return The filename of the signal data.
     */
    public String getSignalFilename() {
        return signalFilename;
    }

    /**
     * Sets the filename where the signal data is stored.
     *
     * @param signalFilename The filename to set for the signal data.
     */
    public void setSignalFilename(String signalFilename) {
        this.signalFilename = signalFilename;
    }

    /**
     * Returns the type of the signal (EMG or Accelerometer).
     *
     * @return The type of the signal.
     */
    public SignalType getSignalType() {
        return signalType;
    }

    /**
     * Sets the type of the signal (EMG or Accelerometer).
     *
     * @param signalType The signal type to set.
     */
    public void setSignalType(SignalType signalType) {
        this.signalType = signalType;
    }

    /**
     * Stores the signal data in a file based on its type.
     */
    public void storeSignalInFile() {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String ruta = null;
        try {
            // Different paths based on the signal type
            if (this.signalType == SignalType.EMG) {
                ruta = "MeasurementsEMG\\" + signalFilename;
            } else if (this.signalType == SignalType.Accelerometer) {
                ruta = "MeasurementsAccelerometer\\" + signalFilename;
            }

            // Store the signal values in the file
            String contenido = getSignalValues(samplingrate).toString();
            File file = new File(ruta);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(contenido);

        } catch (IOException ex) {
            Logger.getLogger(Signal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Signal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Retrieves the signal values at the specified sampling rate.
     * @param samplingRate the sampling rate for the signal.
     * @return a list of signal values at the specified rate.
     */
    public LinkedList<Integer> getSignalValues(int samplingRate) {
        LinkedList<Integer> result = new LinkedList<>();
        for (int j = 0; j < samplingRate; j++) {
            int blockSize = samplingRate;
            for (int i = 0; i < blockSize; i++) {
                int value = j * blockSize + i;
                result.add(values.get(value));  // Add the values to the list
            }
        }
        return result;
    }

    /**
     * Converts the list of signal values into a space-separated string.
     * @return a string representation of the signal values.
     */
    public String valuesToString() {
        StringBuilder message = new StringBuilder();
        String separator = " ";

        for (int i = 0; i < values.size(); i++) {
            message.append(values.get(i));
            if (i < values.size() - 1) {
                message.append(separator);
            }
        }

        return message.toString();
    }

    /**
     * Parses a space-separated string into a list of signal values.
     * @param str the input string representing signal values.
     * @return a list of integers parsed from the input string.
     */
    public List<Integer> stringToValues(String str) {
        values.clear(); // Clear the list before adding new values.
        String[] tokens = str.split(" "); // Split the string by spaces
        int size = tokens.length;
        if (size > 2) {
            for (int i = 0; i < size; i++) {
                try {
                    values.add(Integer.parseInt(tokens[i])); // Convert each part to an Integer and add to the list
                } catch (NumberFormatException e) {
                    // Handle the error if a value is not a valid Integer
                    System.out.println("Error al convertir el valor: " + tokens[i]);
                }
            }
        }
        return values;
    }

    /**
     * Adds a list of new signal values to the existing signal.
     * @param values a list of new signal values to add.
     */
    public void addValues(LinkedList<Integer> values){
        this.values.addAll(values);
    }

    @Override
    public String toString() {
        return "Signal{" +
                "values=" + values +
                ", signalFilename='" + signalFilename + '\'' +
                ", signalType=" + signalType +
                '}';
    }
}

