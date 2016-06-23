package bayeos.serialframe;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bayeos.device.SerialDeviceInterface;
import bayeos.frame.callback.ReadCallBack;
import bayeos.serial.ComPortDevice;
import static bayeos.serialframe.SerialFrameConstants.*;
import static org.junit.Assert.*;

public class SeriaDeviceTest {
	
		
	public final String comPort = "COM10";	
	SerialFrameDevice dev = null;
	 
	@Before
	public void setUp() throws Exception {									
		SerialDeviceInterface com = new ComPortDevice(comPort);			
		if (!com.open()){
			fail("Failed to open device");
		}		
		dev = new SerialFrameDevice(com);		
	}

	@After
	public void tearDown() throws Exception {
		dev.close();				
	}
	
	
	/**
	 * Test async call to get version from logger over serial line 
	 */
	@Test	
	public void readCommPortAsync()  {
	
		@SuppressWarnings("rawtypes")		
		ReadCallBack callBack = new ReadCallBack<String>(){			
			@Override
			public void onData(byte[] data) {			
				super.onData(data); // sets running flag
				value = new String(Arrays.copyOfRange(data, 2, data.length));
				System.out.println("Response:" + value);
			}
		};					
				
		System.out.println("Sending logger command 'Get name'. ");
		try {
			dev.writeFrame(api_data, new byte[] {0x2,0x9});
		} catch (IOException e) {
			fail(e.getMessage());
		}													
		dev.readFrame(callBack);	
		
		assertFalse(callBack.hasError());	
		System.out.println(callBack.getValue());							
		
	}
}
