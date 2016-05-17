package bayeos.frame.data;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.frame.ByteFrame;
import bayeos.frame.FrameConstants;
import bayeos.frame.FrameConstants.NumberType;

public class DataFrame implements ByteFrame {
	
	private Number[] values;
	private NumberType numberType;
		
	public DataFrame(NumberType numberType, Number... values){
		this.numberType = numberType;
		this.values = values;
	}

	public byte[] getBytes(){
		byte[] n = new byte[2+values.length*numberType.getLength()];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put(FrameConstants.DataFrame);
		bf.put((byte)(FrameConstants.FrameWithoutOffset + numberType.getIndex()));
		for(Number nu:values){
			bf.put(FrameConstants.toByte(numberType, nu));
		}		
		return n;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		DataFrame a = new DataFrame(NumberType.UInt8,10,10,12);				
	}
}
