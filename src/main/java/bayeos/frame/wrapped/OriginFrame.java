package bayeos.frame.wrapped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.frame.ByteFrame;
import bayeos.frame.FrameConstants;

public class OriginFrame implements ByteFrame {
	
	private String origin;
	private byte[] payload;

	public OriginFrame(String origin, byte[] payload) {
		this.origin = origin;
		this.payload = payload;
	}
	
	public OriginFrame(String origin, ByteFrame b) {		
		this(origin,b.getBytes());
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public byte[] getBytes() {
		byte[] n = new byte[1 + 1 + origin.length() + payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)FrameConstants.OriginFrame);
		bf.put((byte)origin.length());
		bf.put(origin.getBytes());
		bf.put(payload);
		return n;
	}

}
