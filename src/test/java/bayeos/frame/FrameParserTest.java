package bayeos.frame;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.junit.Test;

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
		
		
	

}
