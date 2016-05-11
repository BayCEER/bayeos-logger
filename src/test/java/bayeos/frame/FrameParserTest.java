package bayeos.frame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

import bayeos.frame.FrameConstants.NumberType;
import bayeos.frame.data.DataFrame;
import bayeos.frame.data.IndexFrame;
import bayeos.frame.data.LabeledFrame;
import bayeos.frame.wrapped.DelayedFrame;
import bayeos.frame.wrapped.MillisecondTimestampFrame;
import bayeos.frame.wrapped.OriginFrame;
import bayeos.frame.wrapped.TimestampFrame;
import bayeos.serialframe.SerialFrameHandler;
import bayeos.serialframe.SerialFrameParser;




public class FrameParserTest {
	
	
	@Test
	public void testSerialRead()  {
		InputStream in = null;
		in = getClass().getResourceAsStream("/frames2.bin");
		
						
		FrameHandler fh = new DefaultFrameHandler() {
			@Override
			public void onDataFrame(byte type, Hashtable<String, Float> values) {
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
			public void onDataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {				
				assertEquals("dummy",origin);
				assertNotNull(timeStamp);
				assertEquals(3, values.size());
				assertEquals(1.0F, (float)values.get("1"),0.1F);
				assertEquals(2.0F, (float)values.get("2"),0.1F);
				assertEquals(3.0F, (float)values.get("3"),0.1F);
			}
		});	
		p.parse(f.getBytes());
				
	}
	
	@Test
	public void msgFrame() throws FrameParserException {
		StringFrame msg = new StringFrame(FrameConstants.Message, "Hello again");		
		FrameParser p = new FrameParser(new DefaultFrameHandler("MyOrigin") {
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
		FrameParser p = new FrameParser(new DefaultFrameHandler("MyOrigin") {
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
				
		DelayedFrame delayed = new DelayedFrame(5000, new DataFrame(NumberType.UInt8,12,2).getBytes());		
		MillisecondTimestampFrame ms = new MillisecondTimestampFrame(now, new IndexFrame(NumberType.Int32,10,10).getBytes());
						
		DefaultFrameHandler df = new DefaultFrameHandler("MyOrigin") {
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {								
				System.out.println(timeStamp);
			}
		};
		
	
		
		FrameParser p = new FrameParser(df);		
		p.parse(ms.getBytes());
		p.parse(delayed.getBytes());
		p.parse(delayed.getBytes());
		p.parse(ms.getBytes());		
		p.parse(delayed.getBytes());
		
		
	}
	
	@Test
	public void timeStampDelayed() throws FrameParserException {
		long secs = DateAdapter.getSeconds(new Date());
					
		final Date ts = DateAdapter.getDate(secs);
		
		TimestampFrame tf = new TimestampFrame(ts,new DelayedFrame(10000,new IndexFrame(NumberType.Int32,10,10).getBytes()).getBytes());
		
		DefaultFrameHandler df = new DefaultFrameHandler("MyOrigin") {
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {												
				assertEquals(ts.getTime()-10000,timeStamp.getTime());
			}
		};
		
		FrameParser p = new FrameParser(df);		
		p.parse(tf.getBytes());
		
	}
	
	@Test
	public void labeledFrame() throws FrameParserException, ScriptException {
		Map<String,Number> values = JSEngine.getMap("{'c1':1.0,'c11':11.0,'c3':3.0}");
		LabeledFrame f = new LabeledFrame(NumberType.UInt8,values);		 
		DefaultFrameHandler df = new DefaultFrameHandler("MyOrigin") {
				@Override
				public void onDataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {												
						assertEquals(3, values.size());
				}
		};			
		new FrameParser(df).parse(f.getBytes());
		
	}
		
	
	

}
