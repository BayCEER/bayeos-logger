package bayeos.serialframe;



public final class SerialFrameConstants {
	
	public static final byte frameDelimeter = 0x7e;
	public static final byte api_data = 0x1;
	public static final byte api_ack = 0x2;
	public static final byte escapeByte = 0x7d;
	
	public final static int ack_ok = 0x1;
	public final static int ack_nok = 0x2;
	public final static int ack_break = 0x3;
	
	public static final byte[] ACK_FRAME = { frameDelimeter, 0x1, api_ack, ack_ok, (byte) 0xFC };
	public static final byte[] NACK_FRAME = {frameDelimeter, 0x1, api_ack, ack_nok, (byte) 0xFB };
	public static final byte[] BREAK_FRAME = { frameDelimeter, 0x1, api_ack, ack_break, (byte) 0xFA };

	private SerialFrameConstants(){
		
	}
	
}
