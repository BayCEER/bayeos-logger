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
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import bayeos.frame.FrameParserException;
import bayeos.frame.Parser;
import bayeos.serialdevice.ComPortDevice;

public class AsyncLoggerTest {

	AsyncLogger logger = null;
	ComPortDevice com = null;
	Logger log = Logger.getRootLogger();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp()  {
		log.setLevel(Level.DEBUG);
		log.debug("Setup");
		com = new ComPortDevice();		
		try {
			com.openLastPort();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		logger = new AsyncLogger(com);
	}

	@After
	public void tearDown()  {
		log.debug("Tear down");
		logger.stop();
		com.close();		
	}

	@Test
	@Ignore
	public void setName() {
		try {
			String name = NameGenerator.generateName() + " " + NameGenerator.generateName();			
			logger.setName(name);
			String newName = logger.getName();
			assertEquals(name, newName);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	public void setTime() {
		try {
			Date a = new Date();			
			logger.setTime(a);
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
			}
			Date b = logger.getTime();			
			assertTrue(b.after(a));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	@Ignore
	public void setSamplingInterval()  {		
		try {
		    Random rand = new Random();
			int i = 60 + rand.nextInt(120);				
			int d = logger.setLoggingInterval(i);			
			assertEquals(i,d);
		} catch (IOException e) {
			fail(e.getMessage());
			
		}		
						
	}
	
	@Test 
	@Ignore
	public void getVersion() throws Exception {
		String version = logger.getVersion();
		assertNotNull(version);		
	}
	
	@Test
	@Ignore
	public void sendBreak() {
		try {
			logger.breakSocket();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@Ignore
	public void readAndParseBulk() throws IOException, FrameParserException {
							
			File file = testFolder.newFile("BAYEOS.DB");
			file.deleteOnExit();			
			log.debug("Dumping to " + file.getAbsolutePath());
			RandomAccessFile ra = new RandomAccessFile(file, "rw");
			BulkWriter bWriter = new BulkWriter(ra);
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
				read = read + bulk.length-5;												
												
			}
			ra.close();
			log.debug("Read:" + read + " Bytes");			
			
			BulkReader reader = new BulkReader(new FileInputStream(file));									
			byte[] data = null;			
			while ((data = reader.readData())!=null){
				Parser.parse(data);
			}																								
	} 

	@Test
	@Ignore
	public void testLiveMode() {		
			try {
				logger.getVersion();
				logger.startLiveData();	
				long f = 0;
				while(true) {
					byte[] b = logger.readData();
					log.debug("Frame: " + f + " Content:" + Parser.parse(b));
					if (++f==2) break;															
				}				
				logger.stopLiveData();
												
			} catch (IOException | FrameParserException  e) {
				log.error(e.getMessage());
				fail(e.getMessage());
			}
	}

}
