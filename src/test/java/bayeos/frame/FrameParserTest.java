package bayeos.frame;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import org.junit.Test;

import bayeos.frame.FrameConstants.NumberType;
import bayeos.frame.data.DataFrame;
import bayeos.frame.wrapped.OriginFrame;
import bayeos.frame.wrapped.DelayedFrame;
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
	public void originFrame() throws FrameParserException{
		OriginFrame f = new OriginFrame("dummy", new DataFrame(NumberType.Float32, 1.0F,2.0F,3.0F).getBytes());		
		FrameParser p = new FrameParser(new DefaultFrameHandler() {
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<Integer, Float> values, Integer rssi) {				
				assertEquals("dummy",origin);
				assertNotNull(timeStamp);
				assertEquals(3, values.size());
				assertEquals(1.0F, (float)values.get(1),0.1F);
				assertEquals(2.0F, (float)values.get(2),0.1F);
				assertEquals(3.0F, (float)values.get(3),0.1F);
			}
		});	
		p.parse(f.getBytes());
				
	}
	
	@Test
	public void msgFrame() throws FrameParserException {
		StringFrame msg = new StringFrame(FrameConstants.Message, "Hello again");		
		FrameParser p = new FrameParser(new DefaultFrameHandler() {
			@Override
			public void onMessage(String message) {
					assertEquals("Hello again",message);
			}
		});
		p.parse(msg.getBytes());
	}
	
	
	@Test
	public void nullBytesError() throws FrameParserException {
		StringFrame msg = new StringFrame(FrameConstants.Error, "Null bytes\000\000");				
		FrameParser p = new FrameParser(new DefaultFrameHandler() {
			@Override
			public void onMessage(String message) {
					assertEquals("Null bytes",message);
			}
		});
		p.parse(msg.getBytes());
		 
	}
	
	
	
	@Test
	public void delayedFrame() throws FrameParserException {
		final Date now = new Date();
		
		DelayedFrame f = new DelayedFrame(5000, new DataFrame(NumberType.UInt8,12,2).getBytes());
		
		DefaultFrameHandler df = new DefaultFrameHandler() {
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<Integer, Float> values, Integer rssi) {				
				assertTrue(now.getTime()>timeStamp.getTime());			
			}
		};
		
		FrameParser p = new FrameParser(df);	
		p.parse(f.getBytes());
		p.parse(f.getBytes());
		
	}
		
		
	

}
