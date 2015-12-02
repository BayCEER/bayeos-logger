package bayeos.frame;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import org.junit.Test;

import bayeos.frame.FrameConstants.NumberType;
import bayeos.frame.data.DataFrame;
import bayeos.frame.wrapped.OriginFrame;
import bayeos.serialframe.SerialFrameHandler;
import bayeos.serialframe.SerialFrameParser;



public class FrameParserTest {
	
	
	@Test
	public void testSerialRead()  {
		InputStream in = null;
		in = getClass().getResourceAsStream("/frames2.bin");
		
						
		FrameHandler fh = new DefaultFrameHandler() {
			@Override
			public void onDataFrame(byte type, Hashtable<Integer, Float> values) {
				System.out.println(getTimeStamp());
			}
		};
		
		final  FrameParser fp = new FrameParser(fh);				
						
		SerialFrameHandler h = new SerialFrameHandler() {			
			@Override
			public void onData(byte api, byte[] payload) {															
				try {							
					fp.parse(payload);					
				} catch (FrameParserException e) {
					System.out.println(e.getMessage());
				}
			}

			@Override
			public void onError(String msg) {
				System.out.println(msg); 				
			}			
									
		};		
		
		SerialFrameParser p = new SerialFrameParser(h);						
		p.parse(in);
		try {
			in.close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void originFrame(){
		
		OriginFrame f = new OriginFrame("dummy", new DataFrame(NumberType.Float32, 1.0F,2.0F,3.0F).getBytes());
		
		DefaultFrameHandler df = new DefaultFrameHandler() {
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<Integer, Float> values, Integer rssi) {				
				System.out.println(origin + ":" + timeStamp + ":" + values);			
			}
		};
		
		FrameParser p = new FrameParser(df);
		try {
			p.parse(f.getBytes());
		} catch (FrameParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
		
	

}
