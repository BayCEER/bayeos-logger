package bayeos.serialframe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SerialDeviceTest {
	
		
	public final String comPort = "COM10";
	
	public final Logger logger = Logger.getRootLogger();
	
	ISerialFrame dev = null;
	
	ComPortDevice com;
	 
	@Before
	public void setUp() throws IOException {
		logger.setLevel(Level.DEBUG);
		com = new ComPortDevice();			
		com.open(comPort);
		dev = new SerialFrameDevice(com);		
	}

	@After
	public void tearDown() throws IOException {	
		com.close();
	}
	
	
	@Test	
	public void read() throws InterruptedException  {
		System.out.println("Sending logger command 'Get name'. ");
		try {
			dev.writeFrame(new byte[] {0x2,0x9});			
			Thread.sleep(2000);
			assertTrue(dev.available());			
			System.out.println(new String(dev.readFrame()));						
		
		} catch (IOException e) {
			fail(e.getMessage());
		}						
	}
	
	
}
