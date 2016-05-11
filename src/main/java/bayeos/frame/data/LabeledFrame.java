package bayeos.frame.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import bayeos.binary.ByteArray;
import bayeos.frame.FrameConstants;
import bayeos.frame.FrameConstants.NumberType;

public class LabeledFrame {
	
	private Map<String,Number> values;
	private NumberType numberType;
	
	public LabeledFrame(NumberType numberType, Map<String,Number> values){
		this.numberType = numberType;
		this.values = values;
	}
	
	public byte[] getBytes(){
		int len = 2;
		for (String label:values.keySet()){
			len = len + 1 + label.length() + numberType.getLength();
		}
		byte[] b = new byte[len];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put(FrameConstants.DataFrame);
		bf.put((byte)(FrameConstants.FrameWithLabel + numberType.getIndex()));	
		for(Map.Entry<String, Number> e: values.entrySet()){			
			bf.put(ByteArray.toByteUInt8(e.getKey().length()));
			bf.put(e.getKey().getBytes());
			bf.put(FrameConstants.toByte(numberType, e.getValue()));
		}		
		return b;
	}

}
