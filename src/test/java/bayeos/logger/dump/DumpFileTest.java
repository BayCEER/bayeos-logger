package bayeos.logger.dump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class DumpFileTest {
	
	
	
	@Test
	public void testFileProperties() throws IOException {
		String filePath = "src/test/resources/DummyA_2017-09-26_15-24-58.db";		
		DumpFile d = new DumpFile(filePath);	
		assertEquals("DummyA",d.getOrigin());
		assertNotNull(d.length());
		assertNotNull(d.getLastModified());
	}
	
	@Test
	public void testInfo() throws IOException {
		String filePath = "src/test/resources/DummyA_2017-09-26_15-24-58.db";		
		DumpFile d = new DumpFile(filePath);				
		assertNotNull(d.getInfo());		
		System.out.println(d.getInfo());
	}
	

}
