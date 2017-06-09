package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.*;

import java.io.IOException;

import bayeos.binary.CheckSum;
import bayeos.serialdevice.ISerialDevice;



public class SerialFrameDevice implements ISerialFrame {
	
	
	public static boolean DEBUG = false;

	private ISerialDevice device;	
		

	public SerialFrameDevice(final ISerialDevice device) {		
		this.device = device;
	}
	
	
	@Override
	public void writeFrame(byte[] data) throws IOException {		
		writeFrame(api_data,data);		
	}



	@Override
	public byte[] readFrame() throws IOException {
		return readFrame(api_data);
	}
	
	@Override
	public void stop() throws IOException {
		device.write(BREAK_FRAME);		
	}
		

	
	private void writeFrame(byte apiType, byte[] data) throws IOException {			
		device.write(SerialFrameEncoder.encodePayload(apiType, data));
		readFrame(api_ack);
	}
	
	
			
			
	private byte[] readFrame(byte apiType) throws IOException {				
		if (DEBUG) System.out.println("Waiting on start byte ...");
		while (true){
			int b = device.read();			
			if (b == frameDelimeter) {
				if (DEBUG) System.out.println("Start byte received.");
				break; 
			}			
		}
		
		// Read length
		int length = readByteEscaped();		
		if (length == 0) {
			throw new IOException("Payload length 0 not allowed.");			
		}
		
		// Read api
		int api = readByteEscaped();
		
		// Skip invalid response types 		
		if (apiType!=api){
			if (DEBUG) System.out.println("Skipping invalid response.");
			return readFrame(apiType);
		}
		
		
		CheckSum chkCalculated = new CheckSum();
		chkCalculated.addByte((byte) api);
							
		// Read payload 
		byte[] payload = new byte[length];				
		int p = 0;
		for (int i=0;i<length;i++){
			p = readByteEscaped();						
			payload[i] = (byte) p; 
			chkCalculated.addByte((byte) p);			
		}
		
		// Read checksum 
		int chkExpected = readByteEscaped();					
		if (chkCalculated.oneByte() != chkExpected){
			device.write(NACK_FRAME);
			throw new IOException("Invalid checksum. Expected:" + chkExpected + " Calculated:" + chkCalculated.oneByte());			
		} 	

		if (DEBUG) System.out.println("Writing ack ...");		
		device.write(ACK_FRAME);
		return payload;		 
	}
	
	private int readByteEscaped() throws IOException {
		int b = device.read();			 
	    if (b == escapeByte){
	    	b =  0x20 ^ device.read();	
	    } 	    
	    return b;				
	}






	
		
		
	
	

	
	
	

}
