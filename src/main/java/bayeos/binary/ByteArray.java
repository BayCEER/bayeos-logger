package bayeos.binary;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ByteArray {
	
	public static ByteOrder order = ByteOrder.LITTLE_ENDIAN;
	
		
	// Double 64 
	public static byte[] toByteDouble(Double value){
		byte[] b = new byte[8];
		ByteBuffer bb = ByteBuffer.wrap(b);		
		bb.order(order);
		bb.putDouble(value);
		return b;		
	}

	public static Double fromByteDouble(byte[] value){
		return fromByteDouble(value,0);
	}
	
	public static Double fromByteDouble(byte[] value, int offset){
		ByteBuffer bb = ByteBuffer.wrap(value,offset,8);
		bb.order(order);
		return bb.getDouble();
	}
	
		
	// Float32
	public static byte[] toByteFloat32(Float value){
		byte[] b = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(b);		
		bb.order(order);
		bb.putFloat(value);
		return b;		
	}
	
	public static Float fromByteFloat32(byte[] value){
		return fromByteFloat32(value,0);
	}
	
	public static Float fromByteFloat32(byte[] value, int offset){
		ByteBuffer bb = ByteBuffer.wrap(value,offset,4);
		bb.order(order);
		return bb.getFloat();
	}
	
	
	// Int32	
	public static byte[] toByteInt32(Integer value){
		byte[] b = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(order);
		bb.putInt(value);
		return b;
	}
	
	
	public static Integer fromByteInt32(byte[] value){
		return fromByteInt32(value, 0);
	}
	
	public static Integer fromByteInt32(byte[] value, int offset){
		ByteBuffer bb = ByteBuffer.wrap(value,offset,4);
		bb.order(order);
		return bb.getInt();		
	}
	
	
	// UInt32 
	public static byte[] toByteUInt32(Long value) {		
		if (value < 0) throw new IllegalArgumentException("Value is out of valid range.");
		byte[] b = new byte[4];		
		if (order == ByteOrder.BIG_ENDIAN) {
			b[0] = (byte) ((value >> 24) & 0xff);
			b[1] = (byte) ((value >> 16) & 0xff);
			b[2] = (byte) ((value >> 8) & 0xff);
			b[3] = (byte) (value & 0xff);
		} else {
			b[3] = (byte) ((value >> 24) & 0xff);
			b[2] = (byte) ((value >> 16) & 0xff);
			b[1] = (byte) ((value >> 8) & 0xff);
			b[0] = (byte) (value & 0xff);	
		}		
		return b;
	}
	
	public static Long fromByteUInt32(byte[] value){
		return fromByteUInt32(value, 0);
	}
	
	public static Long fromByteUInt32(byte[] value, int offset){		
		ByteBuffer bb = ByteBuffer.wrap(value,offset,4);
		bb.order(order);		
		return fromByteUInt32(bb);		
	}
	
	public static Long fromByteUInt32(ByteBuffer bb) {
			Long ret;		
			if (order == ByteOrder.LITTLE_ENDIAN) {
				ret=(bb.get() & 0xffL);
				ret+=(bb.get() & 0xffL) << 8;
				ret+=(bb.get() & 0xffL) << 16;
				ret+=(bb.get() & 0xffL) << 24;
			} else {
				ret=(bb.get() & 0xffL) << 24;
				ret+=(bb.get() & 0xffL) << 16;
				ret+=(bb.get() & 0xffL) << 8;
				ret+=(bb.get() & 0xffL);				
			}
			return ret;
	}
	
			
	// Int16 	
	public static byte[] toByteInt16(Short value){
		byte[] b = new byte[2];
		ByteBuffer bb = ByteBuffer.wrap(b);		
		bb.order(order);
		bb.putShort(value);
		return b;
	}
	
	public static Short fromByteInt16(byte[] value){
		return fromByteInt16(value, 0);		
	}
	
	public static Short fromByteInt16(byte[] value,int offset ){
		ByteBuffer bb = ByteBuffer.wrap(value, offset, 2);
		bb.order(order);
		return bb.getShort();
	}
	
	// UInt16
	public static byte[] toByteUInt16(int value){
		if (value > 65535) throw new IllegalArgumentException();		
		byte[] b = new byte[2];	
		if (order == ByteOrder.LITTLE_ENDIAN) {
			b[1] = (byte) ((value >> 8) & 0xff);
			b[0] = (byte) (value & 0xff);	
		} else {
			b[0] = (byte) ((value >> 8) & 0xff);
			b[1] = (byte) (value & 0xff);
		}			
		return b;
	}
	
	
	
	public static int fromByteUInt16(ByteBuffer bb){		 
		return (int) (bb.getShort() & 0xffff); 
	}
	
	public static int fromByteUInt16(byte[] value){
		ByteBuffer bb = ByteBuffer.wrap(value);
		bb.order(order);
		return fromByteUInt16(bb);
	}
	
	// UInt8
	public static byte toByteUInt8(int value){
		return (byte)value;
	}

	public static int fromByteUInt8(byte value){
		return 0xff & value;
	}
	
	
	
		

	
	public static String toString(byte b){
		return String.format("%02X ", b);
	}
	
	public static String toString(byte[] data) {
		StringBuffer sb = new StringBuffer(data.length);
		for(byte b:data){
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

		
	
	

}
