package bayeos.frame;

import java.nio.ByteBuffer;

public class StringFrame implements ByteFrame {
	String value;
	byte type;
	
	public StringFrame(byte type, String value) {
			this.value = value;
			this.type = type;
	}
	
	public byte[] getBytes(){		
		byte[] b = new byte[value.getBytes().length+1];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put(type);
		bf.put(value.getBytes());
		return b;		
	}

}
