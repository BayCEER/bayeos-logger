package bayeos.frame.wrapped;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.binary.ByteArray;
import bayeos.frame.ByteFrame;
import bayeos.frame.FrameConstants;


public class RoutedFrame implements ByteFrame  {

	protected Integer myId;
	protected Integer panId;
	protected byte[] payload;
	
	
	public RoutedFrame(Integer myId, Integer panId, byte[] payload) {		
		this.myId = myId;
		this.panId = panId;		
		this.payload = payload;
	}
	
	public RoutedFrame(Integer myId, Integer panId,ByteFrame b) {		
		this(myId,panId,b.getBytes());
	}
	
	

	public byte[] getBytes()  {
		byte[] n = new byte[5 + payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)FrameConstants.RoutedFrame);
		bf.put(ByteArray.toByteInt16(myId.shortValue()));
		bf.put(ByteArray.toByteInt16(panId.shortValue()));
		bf.put(payload);		
		return n;
	}

		
}
