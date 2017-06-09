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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import bayeos.frame.FrameParserException;
import bayeos.frame.Parser;
import bayeos.frame.types.MapUtils;
import bayeos.serialframe.ComPortDevice;


public class LoggerDeviceTest {

	public final String comPort = "COM10";

	Logger logger = null;
	ComPortDevice com = null;
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();


	@Before
	public void setUp() throws Exception {
		com = new ComPortDevice(comPort);
		if (!com.open()) {
			fail("Failed to open device");
		}
		logger = new Logger(com);		
	}

	@After
	public void tearDown() throws Exception {					
		com.close();
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
	
	@Test
	public void dataFull() {
		try {
			long bytes = logger.startData(LoggerConstants.DM_FULL);
			long bytesRead = 0;
			long frameCount = 0;			
			while (bytesRead < bytes) {
				try {
					byte data[] = logger.readData();					
					System.out.println(MapUtils.toString(Parser.parse(data)));
					frameCount++;
					if (frameCount % 100 == 0)	System.out.print("+");
					bytesRead += data.length;				
				} catch (IOException e) {
					System.out.println(e.getMessage());					
				} catch (FrameParserException e) {
					System.out.println(e.getMessage());	
				}
			}
			System.out.println(frameCount + " Frames in " + bytesRead + " bytes received");
			

		} catch (IOException e) {
			fail(e.getMessage());
		}

	}
	
	

	
	@Test
	public void sendBreak() {
		try {
			logger.breakSocket();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	
	@Test
	public void readAndParseBulk() throws IOException, FrameParserException {
							
			File file = testFolder.newFile("BAYEOS.DB");
			// file.deleteOnExit();			
			System.out.println("Dumping to " + file.getAbsolutePath());
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			BulkWriter bWriter = new BulkWriter(bout);
			long read = 0;			
			long bytes = logger.startBulkData(LoggerConstants.DM_FULL);
			System.out.println("Reading:" + bytes + " Bytes");
									
			long c = 0;
			while (read < bytes) {
				System.out.println(c++);
				System.out.println( + read + "/" + bytes + " bytes");
				byte[] bulk = logger.readBulk();
				System.out.println("Bulk size:" + bulk.length);
				bWriter.write(bulk);
				bout.flush();
				read = read + bulk.length-5;												
												
			}
			bout.flush();
			bout.close();
			System.out.println("Read:" + read + " Bytes");			
			
			BulkReader reader = new BulkReader(new FileInputStream(file));									
			byte[] data = null;			
			while ((data = reader.readData())!=null){
				Parser.parse(data);
			}
			
																								
	} 
	

	
	

}
