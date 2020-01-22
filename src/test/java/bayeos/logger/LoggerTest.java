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

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import bayeos.frame.FrameParserException;
import bayeos.frame.Parser;
import bayeos.serialdevice.ComPortDevice;


public class LoggerTest {


	Logger logger = null;
	ComPortDevice com = null;
	
	
	public final org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();	

	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();


	@Before
	public void setUp() throws IOException {
		log.setLevel(Level.DEBUG);
		com = new ComPortDevice();
		com.openLastPort();		
		logger = new Logger(com);		
	}

	@After
	public void tearDown() throws IOException {					
		com.close();
	}

	@Test
	public void setName() throws Exception {		
		logger.setName(NameGenerator.generateName());		
	}

	
	@Test
	public void setTime() throws Exception {
		log.debug("Set Time");
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
		log.debug("Set sampling interval");
		logger.setSamplingInterval(60);
		
		assertEquals(60, logger.getSamplingInterval());				
	}
	
	@Test 
	public void getVersion() throws Exception {
		log.debug("Get Version");
		String version = logger.getVersion();
		assertNotNull(version);		
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
			file.deleteOnExit();			
			log.debug("Dumping to " + file.getAbsolutePath());
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			BulkWriter bWriter = new BulkWriter(bout);
			long read = 0;			
			long bytes = logger.startBulkData(LoggerConstants.DM_FULL);
			log.debug("Reading:" + bytes + " Bytes");
									
			long c = 0;
			while (read < bytes) {
				log.debug(c++);
				log.debug( + read + "/" + bytes + " bytes");
				byte[] bulk = logger.readBulk();
				log.debug("Bulk size:" + bulk.length);
				bWriter.write(bulk);
				bout.flush();
				read = read + bulk.length-5;												
												
			}
			bout.flush();
			bout.close();
			log.debug("Read:" + read + " Bytes");			
			
			BulkReader reader = new BulkReader(new FileInputStream(file));									
			byte[] data = null;			
			while ((data = reader.readData())!=null){
				Parser.parse(data);
			}																								
	} 
	
	@Test
	public void testLiveMode() {		
			try {
				logger.getVersion();
				logger.startLiveData();	
				long f = 0;
				while(true) {
					byte[] b = logger.readData();
					if (b!= null) {
						if (++f==2) break;	
						try {
							log.debug("Frame: " + f + " Content:" + Parser.parse(b));
						} catch (FrameParserException e) {
							log.error(e.getMessage());
						}																
					} else {					
						Thread.sleep(1000);					
					}					
				}				
				logger.stopLiveData();
													 				
									
												
			} catch (IOException | InterruptedException e) {
				fail(e.getMessage());
			}
	}

	
	

}
