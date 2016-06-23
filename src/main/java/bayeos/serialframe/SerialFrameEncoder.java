package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.escapeByte;
import static bayeos.serialframe.SerialFrameConstants.frameDelimeter;

import java.io.ByteArrayOutputStream;

import bayeos.binary.CheckSum;

public class SerialFrameEncoder {
	
	
	
	static byte[] encodePayload(byte apiType, byte[] payload){
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(200);		
		bout.write(frameDelimeter);
		writeEscaped(bout, (byte) payload.length);
		writeEscaped(bout, apiType);
		CheckSum s = new CheckSum();
		s.addByte(apiType);
		for(byte i:payload){
			writeEscaped(bout, i);
			s.addByte(i);
		}		
		writeEscaped(bout,(byte) s.get());		
		return bout.toByteArray();		
	}
	
	
				
	
	 
	private static void writeEscaped(ByteArrayOutputStream b, byte value)  {
		if (value == frameDelimeter || value == escapeByte) {
			b.write(escapeByte);
			b.write((byte) (value ^ 0x20));
		} else {
			b.write(value);
		}
	}

}
