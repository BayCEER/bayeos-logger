package bayeos.frame.wrapped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import bayeos.binary.ByteArray;
import bayeos.frame.ByteFrame;
import bayeos.frame.DateAdapter;
import bayeos.frame.FrameConstants;

public class TimestampFrame implements ByteFrame  {
	
	private Date timeStamp;
	protected byte[] payload;
	
	public TimestampFrame(Date timeStamp, byte[] payload) {
		this.timeStamp = timeStamp;	
		this.payload = payload;
	}
	
	public TimestampFrame(Date timeStamp, ByteFrame payload) {
		this(timeStamp,payload.getBytes());
	}


	
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public byte[] getBytes(){
		byte[] n = new byte[5+ payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)FrameConstants.TimestampFrame);
		bf.put(ByteArray.toByteUInt32(DateAdapter.getSeconds(timeStamp)));
		bf.put(payload);
		return n;		
	}
	

}
