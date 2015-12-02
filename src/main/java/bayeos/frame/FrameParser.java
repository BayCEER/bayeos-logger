package bayeos.frame;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Hashtable;

import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;


public class FrameParser {

	private FrameHandler handler;
			
	public FrameParser() {
		
	}
	
	public FrameParser(FrameHandler handler) {
		this.handler = handler;
	}
	
			
	public void parse(byte[] payload) throws FrameParserException {
		
		try {
			if (payload == null || payload.length < 3) return;
			ByteBuffer bf = ByteBuffer.wrap(payload);
			bf.order(ByteOrder.LITTLE_ENDIAN);
			parse(bf);			

		} catch (BufferUnderflowException|BufferOverflowException e) {
			
			throw new FrameParserException(0,"Data:" + ByteArray.toString(payload));
		}
	}

	private void parse(ByteBuffer bf) throws FrameParserException {

		while (bf.remaining() > 1) {
			byte frameType = bf.get();
			switch (frameType) {
			case (FrameConstants.DataFrame):
				parseDataFrame(bf);
				break;
			case (FrameConstants.Command):
				handler.onCommand(bf.get(), getRemaining(bf));
				break;				
			case (FrameConstants.Response):
				handler.onResponse(bf.get(), getRemaining(bf));
				break;
			case (FrameConstants.Message):
				handler.onMessage(new String(getRemaining(bf)));
				break;
			case (FrameConstants.Error):
				handler.onError(new String(getRemaining(bf)));
				break;
			case FrameConstants.RoutedFrame:
				Short myId = bf.getShort();
				Short panId = bf.getShort();
				handler.onRoute(myId, panId);
				break;
			case FrameConstants.RoutedFrameRSSI:
				myId = bf.getShort();
				panId = bf.getShort();								
				int rssi = (bf.get() & 0xff) * -1;				
				handler.onRouteRssi(myId,panId,rssi);
				break;	
			case FrameConstants.DelayedFrame:
				long delay = ByteArray.fromByteUInt32(bf);
				handler.onDelay(delay);
				break;
			case FrameConstants.TimestampFrame:	
				long d = ByteArray.fromByteUInt32(bf);				
				handler.onTimeStamp(DateAdapter.getDate(d).getTime());
				break;
			case FrameConstants.MillisecondTimestampFrame:
				handler.onMillisecond(new Date(bf.getLong()));				
				break;
			case FrameConstants.OriginFrame:
				int length = bf.get() & 0xff;				
				String o = "";
				if (length > 0) {
					byte[] s = new byte[length];
					bf.get(s);
					o = new String(s);					
				}
				handler.onOriginFrame(o);
				break;

			default:
				// log.warn("Unknown frame type:" + frameType);
			}
		}

	}

	private void parseDataFrame(ByteBuffer bf) throws FrameParserException {
		
		byte frameType = bf.get();
		
		int bm = frameType & FrameConstants.ByteMask;				
		if (!(bm == FrameConstants.Float32le || bm == FrameConstants.Int32le || bm == FrameConstants.Int16le || bm == FrameConstants.UInt8)){			
			throw new FrameParserException(1, "Unknown value type:" + bm);
		}
		
		int fm = frameType & FrameConstants.FrameMask;
		if (!(fm == FrameConstants.FrameWithOffset || fm == FrameConstants.FrameWithoutOffset || fm == FrameConstants.FrameWithChannel)){
			throw new FrameParserException(2, "Unknown data frame type:" + fm);
		}
								
		int channel = 0;				
		if (fm == FrameConstants.FrameWithOffset) {
			channel = bf.get() & 0xff;
		}

		Hashtable<Integer, Float> values = new Hashtable<Integer, Float>(30);		
		while (bf.remaining() > 0) {
			// Read Channel
			if (fm == FrameConstants.FrameWithChannel) {
				channel = bf.get() & 0xff;
			} else {
				channel++;
			}
			// Read Value
			switch (bm) {
			case (1): // Float
				values.put(channel, bf.getFloat());
				break;
			case (2): // Integer
				values.put(channel, (float) bf.getInt());
				break;
			case (3): // Short
				values.put(channel, (float) bf.getShort());
				break;
			case (4): // UInt8
				values.put(channel, (float) (bf.get() & 0xff));
				break;			
			}
		}
		handler.onDataFrame(frameType, values);
		handler.onDataFrameEnd();
	}

	public void setFrameHandler(FrameHandler handler) {
		this.handler = handler;
	}
	
	public FrameHandler getHandler() {
		return handler;
	}
	
	
	private byte[] getRemaining(ByteBuffer bf) {				
		byte[] bytes = new byte[bf.remaining()];
		bf.get(bytes, 0, bytes.length);		
		return bytes;		
	}

}
