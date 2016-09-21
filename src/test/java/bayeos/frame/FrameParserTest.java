package bayeos.frame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Hashtable;


import org.junit.Test;

import bayeos.binary.ByteArray;
import bayeos.frame.FrameConstants.NumberType;
import bayeos.frame.data.DataFrame;
import bayeos.frame.data.IndexFrame;
import bayeos.frame.data.LabeledFrame;
import bayeos.frame.wrapped.CheckSumFrame;
import bayeos.frame.wrapped.DelayedFrame;
import bayeos.frame.wrapped.MillisecondTimestampFrame;
import bayeos.frame.wrapped.OriginFrame;
import bayeos.frame.wrapped.RoutedFrame;
import bayeos.frame.wrapped.RoutedOriginFrame;
import bayeos.frame.wrapped.TimestampFrame;
import bayeos.serialframe.SerialFrameHandler;
import bayeos.serialframe.SerialFrameParser;



public class FrameParserTest {
		
	//@Test
	public void testSerialRead()  {
		InputStream in = null;
		in = getClass().getResourceAsStream("/frames2.bin");
		
		assertNotNull(in);
		
						
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
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {				
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
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {								
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
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {												
				assertEquals(ts.getTime()-10000,timeStamp.getTime());
			}
		};
		
		FrameParser p = new FrameParser(df);		
		p.parse(tf.getBytes());
		
	}
	
	@Test
	public void labeledFrame() throws FrameParserException {
		LabeledFrame f = new LabeledFrame(NumberType.UInt8,"{'c1':1.0,'c11':11.0,'c3':3.0}");		 
		DefaultFrameHandler df = new DefaultFrameHandler("MyOrigin") {
				@Override
				public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {												
						assertEquals(3, values.size());
						assertEquals(1f,values.get("c1"),0.1);
						assertEquals(11f,values.get("c11"),0.1);
						assertEquals(3f,values.get("c3"),0.1);						
				}
								
		};			
		new FrameParser(df).parse(f.getBytes());
		
	}
	
	@Test
	public void routedJunkFrames() {
		// First frame corrupt
		RoutedFrame rf1 = new RoutedFrame(1, 2, new byte[]{0x7f,0x7f});
		// RoutedFrame rf1 = new RoutedFrame(2, 40, new byte[]{});
		// Second frame ok same type
		Date d = new Date();
		final long secs = DateAdapter.getSeconds(d);		
		final Date ts = DateAdapter.getDate(secs);
				
		RoutedFrame rf2 = new RoutedFrame(3, 4, new MillisecondTimestampFrame(ts, new IndexFrame(NumberType.Int32,10,10).getBytes() ));				
		
		// Merge two Frames				
		ByteBuffer bf = ByteBuffer.allocate(rf1.getBytes().length + rf2.getBytes().length);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put(rf1.getBytes());
		bf.put(rf2.getBytes());
		
							
		DefaultFrameHandler dfh = new DefaultFrameHandler("Sender") {
			@Override
			public void newOrigin(String origin) {
				assertEquals("Sender/XBee4:3", origin);
				// System.out.println("Origin:" + origin);
			}
			
			@Override
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {
				assertEquals("Sender/XBee4:3", origin);
				assertEquals(ts.getTime(), timeStamp.getTime());
				assert(rssi == null);
				assertEquals(10.0F, values.get("1"),0.1);
				assertEquals(10.0F, values.get("2"),0.1);
				
				// System.out.println("Data Frame: origin:" + origin + ",time:" + timeStamp + ",values:" + values +",rssi:" + rssi);
			}
						
		};
				
		try {
			new FrameParser(dfh).parse(bf.array());
			fail("FrameParserException not thrown");
		} catch (FrameParserException e) {
			System.out.println(e.getMessage());
		}
		bf.clear();
	}
		
		
	@Test
	public void testBinaryFrame(){
		byte[] b = new byte[9];		
		ByteBuffer bf = ByteBuffer.wrap(b);		
		bf.put(FrameConstants.BinaryFrame);
		bf.put(ByteArray.toByteUInt32(2000L));
		bf.put(new byte[]{0,2,4,6});
		
		FrameParser p = new FrameParser(new DefaultFrameHandler() {
			@Override
			public void binary(long pos, byte[] value) {
				assertEquals(2000L, pos);
				assertEquals(0, value[0]);
				assertEquals(2, value[1]);
				assertEquals(4, value[2]);
				assertEquals(6, value[3]);
			}
		});
		
		try {
			p.parse(b);
		} catch (FrameParserException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void routedOriginFrame(){
		RoutedOriginFrame child = new RoutedOriginFrame("Leaf2", new IndexFrame(NumberType.Float32, 12.0F,13.0F));
		RoutedOriginFrame rf = new RoutedOriginFrame("Leaf1",child);
		FrameParser p = new FrameParser(new DefaultFrameHandler("Root") {
			@Override
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {
				assertEquals("Root/Leaf1/Leaf2", origin);
			}
		});
		try {
			p.parse(rf.getBytes());
		} catch (FrameParserException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void checkSumFrame(){		
		CheckSumFrame f = new CheckSumFrame(new IndexFrame(NumberType.UInt8,1,1,1,1));
		
		FrameParser p = new FrameParser(new DefaultFrameHandler("Root") {
			@Override
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {
					assertEquals(1,values.get("1").intValue());
			}
		});
		try {
			System.out.println("Valid Frame:"); 
			System.out.println(ByteArray.toString(f.getBytes()));
			p.parse(f.getBytes());			
		} catch (FrameParserException e) {
			fail(e.getMessage());
		}
				
		try {
			System.out.println("Invalid Frame:");
			byte[] b = f.getBytes();
			b[3] = 0x2;			
			System.out.println(ByteArray.toString(b));
			p.parse(b);
			fail("Exception not thrown.");
		} catch (FrameParserException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
		
	}
	

}
