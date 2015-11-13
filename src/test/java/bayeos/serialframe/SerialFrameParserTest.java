package bayeos.serialframe;

import java.io.InputStream;

import org.junit.Test;



public class SerialFrameParserTest {
	
	
	@Test
	public void testSerialRead()  {
		InputStream in = null;
		
		in = getClass().getResourceAsStream("/frames2.bin");		
										
						
		SerialFrameHandler h = new SerialFrameHandler() {			
			@Override
			public void onData(byte api, byte[] payload) {
				System.out.print("Type: " + api);
				System.out.println(" BayEOS Frame Type: " + payload[0]);									
			}

			@Override
			public void onError(String msg) {
				// TODO Auto-generated method stub
				
			}											
		};		
		
		SerialFrameParser p = new SerialFrameParser(h);						
		p.parse(in);
		
		
		
				
								
	}
	
	

}
