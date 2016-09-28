package bayeos.frame.wrapped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.binary.ByteArray;
import bayeos.binary.CheckSum;
import bayeos.frame.ByteFrame;
import bayeos.frame.FrameConstants;

public class CheckSumFrame implements ByteFrame{
	
	byte[] payload;
	
	public CheckSumFrame(ByteFrame frame) {
		payload = frame.getBytes();
	}
				
	@Override
	public byte[] getBytes() {
		byte[] n = new byte[3 + payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put(FrameConstants.ChecksumFrame);
		bf.put(payload);
		CheckSum s = new CheckSum();
		s.addByte(FrameConstants.ChecksumFrame);
		for(byte b:payload){
			s.addByte(b);
		}		
		bf.put(ByteArray.toByteUInt16(s.twoByte()));
		return n;
	}


	
	
	
	

}
