package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.ACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.BREAK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.NACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.api_ack;
import static bayeos.serialframe.SerialFrameConstants.api_data;
import static bayeos.serialframe.SerialFrameConstants.escapeByte;
import static bayeos.serialframe.SerialFrameConstants.frameDelimeter;

import java.io.IOException;

import bayeos.binary.ByteArray;
import bayeos.binary.CheckSum;
import bayeos.serialdevice.ISerialDevice;



public class SerialFrameDevice implements ISerialFrame {
	
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SerialFrameDevice.class);

		
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
		try {
		return readFrame(api_data);
		} catch (InvalidApiTypeException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public void breakFrame() throws IOException {
		log.debug("stop()");
		device.write(BREAK_FRAME);
		
	}
		

	
	private void writeFrame(byte apiType, byte[] data) throws IOException {
		log.debug("Write frame:" +ByteArray.toString(data));
		device.write(SerialFrameEncoder.encodePayload(apiType, data));
		
		try {
			readFrame(api_ack);
		} catch (InvalidApiTypeException e) {
			log.error(e.getMessage());
			breakFrame();
			writeFrame(apiType, data);
		}
	}
	
	
			
			
	private byte[] readFrame(byte apiType) throws IOException, InvalidApiTypeException {
		
		log.debug("Read " + ((apiType==api_data)?"data":"acknowledge") + " frame");
		
		log.debug("Waiting for start byte");
		while (true){
			int b = device.read();			
			if (b == frameDelimeter) {
				log.debug("Start byte received.");
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
		log.debug("On " + ((api==api_data)?"data":"acknowledge") + " frame");
								
		// Skip invalid response types 		
		if (apiType!=api){
			log.debug("Skipping invalid response.");
			throw new InvalidApiTypeException("Expected:" + String.valueOf(apiType) + " Read:" + String.valueOf(api) );			
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

		log.debug("Writing ack ...");		
		device.write(ACK_FRAME);
		
		if (log.isDebugEnabled()) {
			log.debug("Payload:" + ByteArray.toString(payload));
		}
		return payload;		 
	}
	
	private int readByteEscaped() throws IOException {
		int b = device.read();			 
	    if (b == escapeByte){
	    	b =  0x20 ^ device.read();	
	    } 	    
	    return b;				
	}


//	@Override
//	public boolean available() throws IOException {		
//		return device.available() > 0;
//	}






	
		
		
	
	

	
	
	

}
