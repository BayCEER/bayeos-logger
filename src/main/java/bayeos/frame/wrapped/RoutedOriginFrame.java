package bayeos.frame.wrapped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.frame.ByteFrame;
import bayeos.frame.FrameConstants;

public class RoutedOriginFrame extends OriginFrame implements ByteFrame {
	
	
	public RoutedOriginFrame(String origin, byte[] payload) {
		super(origin, payload);		
	}	
	
	public RoutedOriginFrame(String origin, ByteFrame b) {
		super(origin, b);		
	}

	public byte[] getBytes() {
		byte[] n = new byte[1 + 1 + getOrigin().length() + getPayload().length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)FrameConstants.RoutedOriginFrame);
		bf.put((byte)getOrigin().length());		
		bf.put(getOrigin().getBytes());
		bf.put(getPayload());
		return n;
	}

}
