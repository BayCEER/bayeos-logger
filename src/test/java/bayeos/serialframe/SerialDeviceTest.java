package bayeos.serialframe;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SerialDeviceTest {
	
		
	public final String comPort = "COM10";	
	
	ISerialFrame dev = null;
	 
	@Before
	public void setUp() throws Exception {									
		ComPortDevice com = new ComPortDevice(comPort);			
		if (!com.open()){
			fail("Failed to open device");
		}		
		dev = new SerialFrameDevice(com);		
	}

	@After
	public void tearDown() throws Exception {
	
		
	}
	
	
	@Test	
	public void read() throws InterruptedException  {
		System.out.println("Sending logger command 'Get name'. ");
		try {
			dev.writeFrame(new byte[] {0x2,0x9});			
			System.out.println(new String(dev.readFrame()));			
		} catch (IOException e) {
			fail(e.getMessage());
		}						
	}
}
