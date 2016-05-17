package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.api_data;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bayeos.device.SerialDeviceInterface;
import bayeos.serial.ComPortDevice;
import bayeos.serialframe.SerialFrameInterface.ReadCallback;

public class SeriaDeviceTest {
	
		
	public final String comPort = "COM10";
	
	SerialFrameDevice dev = null;
	SerialDeviceInterface com = null;
	
	@Before
	public void setUp() throws Exception {									
		com = new ComPortDevice(comPort);			
		if (!com.open()){
			fail("Failed to open device");
		}		
		dev = new SerialFrameDevice(com);		
	}

	@After
	public void tearDown() throws Exception {
		com.close();				
	}
	
	
	
	
	/**
	 * Test async call to get version from logger over serial line 
	 */
//	@Test	
//	public void readCommPortAsync() {
//	
//	    String nameExpected = "Dummy Space";	    	    		
//													
//		@SuppressWarnings("rawtypes")		
//		ReadCallback callBack = new AbstractReadCallBack<String>(){			
//			@Override
//			public void onData(byte[] data) {			
//				super.onData(data); // sets running flag
//				value = new String(Arrays.copyOfRange(data, 2, data.length));				
//			}
//		};					
//				
//		System.out.println("Sending logger command 'Get name'. ");
//		dev.writeFrame(api_data, new byte[] {0x2,0x9});													
//		dev.readFrame(callBack);	
//		
//		while(callBack.isRunning()){
//			try {
//				Thread.sleep(250);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}	
//		}
//											
//		
//		assertFalse(callBack.hasError());				
//		assertEquals(nameExpected,callBack.getValue());
//		
//		System.out.println("Close device");		
//		com.close();
//				
//		
//	}
}
