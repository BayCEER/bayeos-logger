package bayeos.frame;

import java.nio.ByteBuffer;

import bayeos.frame.FrameConstants;

public class MsgFrame {
	String message;
	
	public MsgFrame(String msg) {
			this.message = msg;
	}
	
	public byte[] getBytes(){		
		byte[] b = new byte[message.getBytes().length+1];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put(FrameConstants.Message);
		bf.put(message.getBytes());
		return b;		
	}

}
