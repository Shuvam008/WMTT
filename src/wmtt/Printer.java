/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmtt;
import com.fazecast.jSerialComm.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
/**
 *
 * @author shuvam
 */
public class Printer {
    private InputStream inputStreamObj;
    private OutputStream outputStreamObj;
    public  SerialPort SerialPortObj = null;
    public  int DataBits             = 8;
    public  int StopBits             = 1;
    public  int Parity               = SerialPort.NO_PARITY;

    public Printer()
    {
        inputStreamObj=null;
        outputStreamObj=null;
//        SerialPortObj=null;
    }
        
    public int ConnectDevice(int portID, int baudrate){
                String printerPortName = "COM1";
                System.out.println("Port number."+printerPortName);
                
                try {
                    this.SerialPortObj.getCommPort(printerPortName);
                } catch (Exception e) {
                    System.out.println("Port number."+e);
                    return 1;
                }
                

                // Configure the port
//                this.SerialPortObj.setComPortParameters(9600, DataBits, StopBits, Parity);
                this.SerialPortObj.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                this.SerialPortObj.setComPortTimeouts(SerialPortObj.TIMEOUT_WRITE_BLOCKING, 0, 0);

                // Open the port
                if (this.SerialPortObj.openPort()) {
                    System.out.println("Port opened successfully.");
                    return 0;
                } else {
                    
                    System.out.println("Failed to open port.");
                    return 1;
                }
    }
    
    public void printText(String text) throws Exception {
        // Initialize printer (ESC @)
        byte[] initCommand = {27, 64};
        
        try {
            outputStreamObj.write(initCommand);
            outputStreamObj.flush();

            // Convert the text to bytes using the printer's character encoding
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);

            // Send the text to the printer
            outputStreamObj.write(textBytes);
            outputStreamObj.flush();

            // Print and feed line (ESC d n - feed n lines)
            byte[] feedCommand = {27, 100, 1}; // Feed one line
            outputStreamObj.write(feedCommand);
            outputStreamObj.flush();
        } catch (Exception e) {
            System.out.println("Printer Not Print"+ e);
        }

    }
    
    public int DeviceDisconnect(){
        // Close the port                
        try {
            SerialPortObj.closePort();
            System.out.println("Port closed.");
            return 0;
        } catch (Exception e) {
            System.out.println("Port close failed"+e);
            return 1;
        }
                      
    }
}
