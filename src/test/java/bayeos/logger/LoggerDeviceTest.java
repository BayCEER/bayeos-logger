package bayeos.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import bayeos.device.SerialDeviceInterface;
import bayeos.frame.DefaultFrameHandler;
import bayeos.frame.FrameParser;
import bayeos.frame.FrameParserException;
import bayeos.serial.ComPortDevice;
import bayeos.serialframe.SerialFrameDevice;



public class LoggerDeviceTest {

	public final String comPort = "COM10";

	LoggerDevice logger;

	SerialDeviceInterface com = null;
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();


	@Before
	public void setUp() throws Exception {
		com = new ComPortDevice(comPort);
		if (!com.open()) {
			fail("Failed to open device");
		}
		logger = new LoggerDevice(new SerialFrameDevice(com));		
	}

	@After
	public void tearDown() throws Exception {
		logger.close();
		System.out.println("Device closed");
	}

	@Test
	public void setName() throws Exception {
		System.out.println("Set name");
		logger.setName("Hallo");
		System.out.println("Get name");
		assertEquals("Hallo", logger.getName());
	}

	
	@Test
	public void setTime() throws Exception {
		System.out.println("Set Time");
		Date a = new Date();
		logger.setTime(a);
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {

		}
		Date b = logger.getTime();
		assertTrue(b.after(a));
	}
	
	@Test
	public void setSamplingInterval() throws Exception {
		System.out.println("Set sampling interval");
		logger.setSamplingInterval(60);
		
		assertEquals(60, logger.getSamplingInterval());				
	}
	
	@Test 
	public void getVersion() throws Exception {
		System.out.println("Get Version");
		String version = logger.getVersion();
		assertNotNull(version);		
	}
	
//	@Test
//	public void dataFull() {
//		try {
//			long bytes = logger.startData(LoggerConstants.DM_FULL);
//			long bytesRead = 0;
//			long frameCount = 0;			
//			while (bytesRead < bytes) {
//				try {
//					byte data[] = logger.readData();					
//					frameCount++;
//					if (frameCount % 100 == 0)	System.out.print("+");
//					bytesRead += data.length;				
//				} catch (IOException e) {
//					System.out.println(e.getMessage());					
//				}
//			}
//			System.out.println(frameCount + " Frames in " + bytesRead + " bytes received");
//			
//
//		} catch (IOException e) {
//			fail(e.getMessage());
//		}
//
//	}
	
	
//	@Test
//	public void readAndParseBulk() throws Exception {
//							
//			File file = testFolder.newFile("BAYEOS.DB");
//			file.deleteOnExit();			
//			System.out.println("Dumping to " + file.getAbsolutePath());
//			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
//			BulkWriter bWriter = new BulkWriter(bout);
//			long read = 0;			
//			long bytes = logger.startBulkData(LoggerConstants.DM_NEW);
//			System.out.println("Reading:" + bytes + " Bytes");
//			long per=0;
//						
//			while (read < bytes) {
//				byte[] bulk = logger.readBulk();								
//				bWriter.write(bulk);
//				read = read + bulk.length-5;												
//				if (per>bytes/100F){
//					per = 0;					
//					System.out.println("Read:" + Math.round(read/(float)bytes*100) + "%");
//				} else {
//					per = per + bulk.length;
//				}								
//			}
//			bout.flush();
//			bout.close();
//			System.out.println("Read:" + read + " Bytes");			
//			
//			LoggerFileReader reader = new LoggerFileReader(new FileInputStream(file));									
//			byte[] data = null;			
//			while ((data = reader.readData())!=null){
//				frameParser.parse(data);
//			}
//			
//																								
//	}
	
	@Test
	public void sendBreak() {
		try {
			logger.breakSocket();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void dataLive() {
		long n = 5;
		try {
			logger.startLiveData();
			System.out.println("Dumping " + n + " frames in live mode");
			int i = 0;									
			while (true) {								
				byte[] d = logger.readData();
				if (d== null) break;					
				frameParser.parse(d);
				i++;
				if (i == n) {
					logger.stopLiveData();
					break;
				}
			}
			System.out.println(i + " Frames received");
		} catch (IOException | FrameParserException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReset() {
		
		
	}
	
	FrameParser frameParser = new FrameParser(new DefaultFrameHandler() {
		@Override
		public void onDataFrame(byte type, Hashtable<String, Float> values) {					
			super.onDataFrame(type, values);			
			for (Entry<String, Float> data : values.entrySet()) {
				System.out.print(" [" + data.getKey() + "]:" + data.getValue());
			}			
		}
		
		@Override
		public void onMillisecond(Date time) {
			super.onMillisecond(time);
			System.out.println(time);
		}
	});
	

}
