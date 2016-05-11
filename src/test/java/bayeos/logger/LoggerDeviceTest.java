package bayeos.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bayeos.device.SerialDeviceInterface;
import bayeos.serial.ComPortDevice;
import bayeos.serialframe.SerialFrameDevice;


public class LoggerDeviceTest {
	
	
	public final String comPort = "COM10";
	
	LoggerDevice logger;
	
	SerialDeviceInterface com = null;

	@Before
	public void setUp()  throws Exception {
		com = new ComPortDevice(comPort);			
		if (!com.open()){
			fail("Failed to open device");
		}			
		logger = new LoggerDevice(new SerialFrameDevice(com));
		System.out.println("device opened");
				
	}
		
	@After
	public void tearDown() throws Exception {
		com.close();				
		System.out.println("device closed");
	}
	
	@Test
	public void setName() throws Exception {
		System.out.println("Set name");
		logger.setName("Hallo");
		System.out.println("Get name");
		assertEquals("Hallo",logger.getName());
	}
	
	@Test
	public void getName() throws Exception {
		System.out.println("Get name");
		assertNotNull(logger.getName());
	}

}
