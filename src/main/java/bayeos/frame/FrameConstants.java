package bayeos.frame;

import bayeos.binary.ByteArray;

public class FrameConstants {
	
	public static final byte DataFrame = 0x1;	
	public static final byte Command = 0x2;
	public static final byte Response =  0x3;
	public static final byte Message = 0x4;
	public static final byte Error = 0x5;
	
	public static final byte RoutedFrame = 0x6; /* [0x6][MY_ID][PANID][Original Frame] */
	public static final byte DelayedFrame = 0x7; /* [0x7][(unsigned long) delay][Original Frame] */
	public static final byte RoutedFrameRSSI = 0x8; /* [0x8][MY_ID][PANID][RSSI][Original Frame] */
	public static final byte TimestampFrame = 0x9; /* [0x9][(unsigned long) timestamp (sec since 2000-01-01 00:00 GMT)][Original Frame] */
	
	public static final byte BinaryFrame = 0xa; /* [0xa][(unsigned long) pos][binary data] */
	public static final byte OriginFrame = 0xb; /* [origin_length][ORIGIN][Original Frame] */
	public static final byte MillisecondTimestampFrame = 0xc; // [(long) timestamp (milli secs since 1970-01-01 00:00 GMT)][Original Frame]
	public static final byte RoutedOriginFrame = 0xd; /* [0xd][origin_length][ORIGIN][Original Frame] -> Origin is appended to current origin using "/" */
	/* public static final byte GatewayCommand = 0xe; */
	public static final byte ChecksumFrame = 0xf; /* [0xf][Original Frame][checksum_16bit] */

	/* Data Frames with offset */
	public static final byte Float32le = 0x1; // Float 4 Byte	
	public static final byte Int32le = 0x2;	 // Integer 4 Byte
	public static final byte Int16le = 0x3;	 // Short 2 Byte
	public static final byte UInt8 = 0x4;	 // Short 1 Byte
	public static final byte Double64le = 0x5; // Double 8 Byte   
		
	/* Data Frames without offset */
	public static final byte Float32leSequential = 0x21; 	// Float 4 Byte
	public static final byte Int32leSequential = 0x22;	// Integer 4 Byte
	public static final byte Int16leSequential = 0x23;	// Short 2 Byte
	public static final byte UInt8Sequential = 0x24;		// Short 1 Byte
	public static final byte Double64Seqential = 0x25;
		
	/* Bytes with channel */
	public static final byte ChannelFloat32le =  0x41; // Channel 1 Byte followed by value as Float 4 Byte
	public static final byte ChannelInt32le =  0x42; // Channel 1 Byte followed by value as Int 4 Byte
	public static final byte ChannelInt16le =  0x43; // Channel 1 Byte followed by value as Int 2 Byte
	public static final byte ChannelUInt8 =  0x44; // Channel 1 Byte followed by value as Short 1 Byte
	public static final byte ChannelDouble64le =  0x45; // Channel 1 Byte followed by value as Double 8 Byte	
	
	

	public static final int ByteMask =  0x0f; 	
	public static final int FrameMask = 0xf0; 	
	
	public static final int FrameWithOffset = 0x0; 	
	public static final byte FrameWithoutOffset = 0x20; 		
	public static final byte FrameWithChannel = 0x40;
	public static final byte FrameWithLabel = 0x60;
		
		     

	
	
	public final static String getFrameName(byte value){
			
		switch (value) {
		case DataFrame: return "DataFrame";	
		case Command: return "Command";	
		case Response: return "Response";	
		case Error: return "Error";			
		case RoutedFrame: return "RoutedFrame";
		case DelayedFrame: return "DelayedFrame";
		case RoutedFrameRSSI: return "RoutedFrameRSSI";
		case BinaryFrame: return "BinarayFrame";
		case TimestampFrame: return "TimestampFrame";
		case OriginFrame: return "OriginFrame";
		case MillisecondTimestampFrame: return "MillisecondTimestampFrame";
		case RoutedOriginFrame: return "RoutedOriginFrame";
		case ChecksumFrame: return "ChecksumFrame";
		
		default:
			return "Unknown";
		}
				
	}
	
	
	public static byte[] toByte(NumberType type, Number value){		
		switch(type) {
		
		case Float32:
			return ByteArray.toByteFloat32((Float)value);
		case Int32:
			return ByteArray.toByteInt32((Integer) value);
		case Int16:
			return ByteArray.toByteInt16((Short)value);
		case UInt8:
			byte[] b = new byte[1];
			b[0] = value.byteValue();
			return b;			
		case UInt32:
			return ByteArray.toByteUInt32((Long)value);
		case Long64:
			
		
		default:
			return null;
		}
			
	}
	
	
	public static enum NumberType {
		
	    Float32(0x1,4), 
	    Int32(0x2,4), 
	    Int16(0x3,2), 
	    UInt8(0x4,1), 
	    UInt32(0x5,4), 
	    Long64(0x6,8);
        
        
		private int index;
		
		public byte getIndex() {
			return (byte) index;
		}

		private int length;		
		
		public int getLength() {
			return length;
		}

		private NumberType(int index, int length) {
                this.index = index;
                this.length = length;
        }
        
        
};  
	 
}
