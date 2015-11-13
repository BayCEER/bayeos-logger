package bayeos.frame.data;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import bayeos.binary.ByteArray;
import bayeos.frame.FrameConstants;
import bayeos.frame.FrameConstants.NumberType;

public class IndexFrame {
	
	private HashMap<Integer,Number> values;
	private NumberType numberType;
		
	public IndexFrame(NumberType numberType, HashMap<Integer, Number> values){
		this.numberType = numberType;
		this.values = values;
	}

	public byte[] getBytes(){
		byte[] n = new byte[2+values.size()*(numberType.getLength()+1)];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put(FrameConstants.DataFrame);
		bf.put((byte)(FrameConstants.FrameWithChannel + numberType.getIndex()));		
		for(Map.Entry<Integer, Number> e: values.entrySet()){
			bf.put( ByteArray.toByteUInt8(e.getKey()));
			bf.put( FrameConstants.toByte(numberType, e.getValue()));
		}		
		return n;
	}
	
	
}
