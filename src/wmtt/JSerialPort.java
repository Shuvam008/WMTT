/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmtt;
import com.fazecast.jSerialComm.*;
import java.util.Scanner; 
import java.util.Arrays;
import java.time.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.OutputStream;

/**
 *
 * @author shuvam
 */
public class JSerialPort {
    	// public  int BaudRate             = 38400;
	public  int DataBits             = 8;
	public  int StopBits             = 1;
	public  int Parity               = SerialPort.NO_PARITY;
	public  SerialPort SerialPortObj = null;
	private final  char[] hexArray   = "0123456789ABCDEF".toCharArray();
	public  long currentTime         = System.currentTimeMillis();
	public  int numRead              = 0;
	public  Boolean isPortOpen       = false;
	
	public JSerialPort(){
		this.SerialPortObj= null;
	}

	public final synchronized int JSerialcommCommunication_Log(String DeviceID, int lvl, String fnMessage ){
//        Currency.Ascrm_WriteLog(DeviceID,fnMessage, lvl);
        //System.out.println(fnMessage);
        return 0;
	}
	
	public  final synchronized byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public  final synchronized String byteToHexV2(byte b) {
		int i = b & 0xFF;
		return Integer.toHexString(i);
	}
	
	public  final synchronized String bytesToHex(byte[] bytes) {
		String str = "";
		int arrayLength = bytes.length;
		for (int counter = 0; counter < arrayLength; counter++) {
			str += "0x" + byteToHexV2(bytes[counter]) + " ";
		}
		return str;
	}// bytesToHex End

	public  final synchronized byte[] ReadData(int responseLength,int minResponseLength,int timeout) {
		Instant after           = null;
		long durationInterval   = 0;
		Instant before          = Instant.now();
		byte[] replyBuffer      = new byte[responseLength];
		Arrays.fill(replyBuffer, (byte)0);
		int totalBytesRead = 0;
		// System.out.println("[ReadData] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"\n");
		   try {
			Thread.sleep(10);   
			while(totalBytesRead < minResponseLength){
				 byte[] readBuffer = new byte[Math.min(SerialPortObj.bytesAvailable(), replyBuffer.length - totalBytesRead)];
				 int bytesRead = SerialPortObj.readBytes(readBuffer, readBuffer.length);
			
				if (bytesRead > 0) {
						System.arraycopy(readBuffer, 0, replyBuffer, totalBytesRead, bytesRead);
						//System.out.println("[ReadData()] BytesAvailable >>> "+bytesAvailable+"\n");
						  totalBytesRead += bytesRead;
						  /*
                           //System.out.println("[ReadData()] Num Read>>> "+numRead+"\n");
                           //System.out.println("[ReadData()] totat Bytes Read >>> "+totalBytesRead+"\n");
                           //System.out.println("[ReadData()] Read Buffer >>> "+bytesToHex(readBuffer)+"\n"); 
						   //System.out.println("[ReadData()] Reply Buffer >>> "+bytesToHex(replyBuffer)+"\n");
					     */
				 }
				 after = Instant.now();
				 durationInterval = Duration.between(before, after).toMillis();
				 if (durationInterval >= timeout) {
					//System.out.println("[ [ActivateCard_Serial()] Operation timeout occurred\n ");
					break;
				 }  
			}
			after = Instant.now();
			durationInterval = Duration.between(before, after).toMillis();
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EXIT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"\n");
			return replyBuffer;
		   
		} catch (Exception e) {
			after = Instant.now();
			durationInterval = Duration.between(before, after).toMillis();
			//System.out.println("[ReadData()] duration Interval >>> "+durationInterval+"\n");
			//System.out.println("[ReadDataString()] Exception Found >>> " + e.getMessage() );
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EXIT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"\n");
			return null;
		}
		
	}

	public  final synchronized int isPortOpen() {	
		if (isPortOpen) {
			return 1;
		} else {
			return 0;
		}
	}   

    public  final synchronized int Connectdevice(int PortNo,int BaudRate) {
			try {
//				this.SerialPortObj = SerialPort.getCommPort("/dev/ttyS" + PortNo);
                                this.SerialPortObj = SerialPort.getCommPort("COM" + PortNo);
			} catch (SerialPortInvalidPortException ex) {
				//System.out.println("[Connectdevice()] Unable to find port");
				return 31;
			}
			this.SerialPortObj.setComPortParameters(BaudRate, DataBits, StopBits, Parity);
			this.SerialPortObj.setFlowControl​(SerialPort.FLOW_CONTROL_DISABLED);
			this.SerialPortObj.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
			//System.out.println("[Connectdevice()] Selected Port               >>> " + this.SerialPortObj.getSystemPortPath​());
			//System.out.println("[Connectdevice()] Selected Baud rate          >>> " + this.SerialPortObj.getBaudRate());
			//System.out.println("[Connectdevice()] Selected Number of DataBits >>> " + this.SerialPortObj.getNumDataBits());
			//System.out.println("[Connectdevice()] Selected Number of StopBits >>> " + this.SerialPortObj.getNumStopBits());
			//System.out.println("[Connectdevice()] Selected Parity             >>> " + this.SerialPortObj.getParity());
			if (true == this.SerialPortObj.openPort()) {
				isPortOpen = true;
//				this.SerialPortObj.flushIOBuffers();
				//System.out.println("[Connectdevice()] Port open sucess ");
				//System.out.println("[Connectdevice()] Device connect successfully ");
				try {
					Thread.sleep(800);
				} catch (Exception e) {
					//TODO: handle exception
				}
				return 0;
			} else {
				//System.out.println("[Connectdevice()] Port open failed ");
				//System.out.println("[Connectdevice()] Device connect failed ");
				return 31;
			}
		
	} 

	public  final synchronized int DisConnectdevice() {
		// Close the port
		try {
			if (true == SerialPortObj.closePort()) {
				SerialPortObj = null;
				isPortOpen = false;
				//System.out.println("[DisConnectdevice()] Port close sucess ");
				//System.out.println("[DisConnectdevice()] Device DisConnect successfully ");
			} else {
				//System.out.println("[DisConnectdevice()] Port close failed ");
				//System.out.println("[DisConnectdevice()] Device DisConnect failed ");
				return 31;
			}

		} catch (SerialPortInvalidPortException ex) {
			//System.out.println("\n Unable to find port");
			return 31;
		}
		return 0;
	}
	
	public  final synchronized int WriteData(byte[] Command) {
		int rtcode = SerialPortObj.writeBytes(Command,Command.length);
		System.out.print("[WriteData()] >>> Bytes Transmitted -> " + rtcode + "\n");
		return rtcode;
	} 

	final synchronized int  cal_crc_loop_CCITT_A( int length,  byte[] p,  int seed, int cd ){
		int i, j;
		int crc = seed;
		for ( i = 1; i < length; ++i ){
			////System.out.println("\n [cal_crc_loop_CCITT_A()]  p["+i+"] >>> 0x"+byteToHexV2(p[ i ] ) );
			crc ^= ( p[i] << 8 );
			for ( j = 0; j < 8; ++j ){
				int temp = crc&0x8000;
				if (  temp>0 )
					crc = (int) ( crc << 1 ) ^ cd;
				else
					crc <<= 1;
			}
		}
		return crc;
	}

	public final synchronized byte getChecksum(byte[] bytes) {
            byte checksum = 0;
            for (byte b : bytes) {
                checksum ^= b;
            }
            return checksum;
    }
	
	public  final synchronized byte[] CommandCycle(byte[] Command,int replyLength,int timeout,int minResponseLength){
		byte[] returnBytes = CommandCycle_Internal(Command, replyLength, minResponseLength, timeout);
		return returnBytes;
	}
	
	public  final synchronized byte[] CommandCycle(String DeviceID, byte[] Command,int replyLength,int timeout){
		String fnMessage = "JSerialcommCommunication " + DeviceID;
		JSerialcommCommunication_Log(DeviceID, 47, fnMessage);
		JSerialcommCommunication_Log(DeviceID, 47, "JSerialcommCommunication [Tx]" + bytesToHex(Command));
		byte[] returnBytes = CommandCycle_Internal(Command, replyLength, replyLength, timeout);
		JSerialcommCommunication_Log(DeviceID, 47, "JSerialcommCommunication [Rx]" + bytesToHex(returnBytes));
		return returnBytes;
	}

	public  final synchronized byte[] CommandCycle_Internal(byte[] Command,int replyLength,int minResponseLength,int timeout) {
		synchronized(this)
                 {
					int rtcode = 0;
					byte[] readData=null;
//					this.SerialPortObj.flushIOBuffers();
					rtcode = this.SerialPortObj.writeBytes(Command,Command.length);
					//System.out.println("[CommandCycle()] Bytes Transmitted >>> " + bytesToHex(Command) );
					//System.out.println("[CommandCycle()] Bytes Transmitted >>> " + rtcode );
					if( Command.length == rtcode ){
			//			    readData = ReadDataV2(1024);
							readData = ReadData(replyLength,minResponseLength,timeout);
							//System.out.println("[CommandCycle()] Reply Bytes >>>" + bytesToHex(readData));
							return readData;
					}else{
						//System.out.println("[CommandCycle()] Write Command Failed >>>" );
						return null;
					}
				} 
	}
}
