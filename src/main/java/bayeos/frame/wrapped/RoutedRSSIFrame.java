package bayeos.frame.wrapped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.binary.ByteArray;
import bayeos.frame.FrameConstants;


public class RoutedRSSIFrame extends RoutedFrame {
	
	private Integer RSSI;			

	public RoutedRSSIFrame(Integer myId, Integer panId, Integer RSSI, byte[] payload) {
		super(myId,panId,payload);
		this.RSSI = RSSI;					
	}
	
	
	
	public byte[] getBytes(){
		byte[] n = new byte[6+payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)FrameConstants.RoutedFrameRSSI);
		bf.put(ByteArray.toByteInt16(myId.shortValue()));
		bf.put(ByteArray.toByteInt16(panId.shortValue()));
		bf.put((byte)RSSI.intValue());		
		bf.put(payload);
		return n;
		
	}
	
}
