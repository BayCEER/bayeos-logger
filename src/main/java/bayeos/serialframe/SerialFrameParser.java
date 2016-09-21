package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.escapeByte;
import static bayeos.serialframe.SerialFrameConstants.frameDelimeter;

import java.io.IOException;
import java.io.InputStream;

import bayeos.binary.ByteArray;
import bayeos.binary.CheckSum;

public class SerialFrameParser {
	
	private SerialFrameHandler handler;	
	
	public static boolean DEBUG = false;
	
	public SerialFrameParser(SerialFrameHandler handler) {
		this.handler = handler;
	}
	
				
	public SerialFrameParser() {		
	}



	public void parse(InputStream in)  {
		
		int b;
		try {
		while((b=in.read())!=-1){				
			if (b == frameDelimeter){
				int length = readByteEscaped(in);
				// Read length
				if (length == 0) {
					handler.onError("Payload length 0 not allowed.");
					continue;
				}
				// Read api
				int api = readByteEscaped(in);
												
				CheckSum chkCalculated = new CheckSum();
				chkCalculated.addByte((byte) api);
									
				// Read payload 
				byte[] payload = new byte[length];				
				int p = 0;
				for (int i=0;i<length;i++){
					p = readByteEscaped(in);
					payload[i] = (byte) p; 
					chkCalculated.addByte((byte) p);
				}
				
				// Read checksum 
				int chkExpected = readByteEscaped(in);					
				if (chkCalculated.oneByte() != chkExpected){
					handler.onError("Invalid checksum. Expected:" + chkExpected + " Calculated:" + chkCalculated.oneByte());
					continue;
				} 
				
				// Fire Events 	
				handler.onData((byte) api, payload);									
			
			}
		}
		
		} catch (IOException e){
			handler.onError(e.getMessage());			
		}
		
		
		
	}

	public SerialFrameHandler getHandler() {
		return handler;
	}

	public void setHandler(SerialFrameHandler handler) {
		this.handler = handler;
	}
	
	
	private int readByteEscaped(InputStream in) throws IOException {
		int b = in.read();			 
	    if (b == escapeByte){
	    	b =  0x20 ^ in.read();	
	    } 	    
	    if (DEBUG) {
	    	System.out.println(getByteString(b));
	    }
	    return b;				
	}
	
		
	
	private String getByteString(int a){
		return String.format("%3d[%x]",a,a);
	}
	
	

}
