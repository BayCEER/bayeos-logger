package bayeos.serialframe;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import bayeos.serialdevice.ComPortDevice;


public class AsyncSerialDeviceTest {

	public final static Logger log = Logger.getRootLogger();
	public AsyncSerialFrameDevice dev;
	public ComPortDevice com;

	@Before
	public void setUp() throws IOException {
		log.setLevel(Level.DEBUG);
		com = new ComPortDevice();
		com.openLastPort();
		dev = new AsyncSerialFrameDevice(this.com);
	}

	@After
	public void tearDown() throws IOException {
		dev.stop();
		com.close();
	}

	@Test
	@Ignore
	public void readBlocking() {
		
		try {
			log.debug("Call get logger name");
			Future<byte[]> frame = dev.writeReadFrame(new byte[] { 0x2, 0x9 });			
			log.debug("Waiting");
			byte[] payload = frame.get();						
			log.debug(new String(payload));												
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		
	}
	
	
}