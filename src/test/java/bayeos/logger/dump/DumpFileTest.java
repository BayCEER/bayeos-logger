package bayeos.logger.dump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

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
	
	
	@Test
	public void testInfoCorrupt() throws IOException {
		String filePath = "src/test/resources/DummyD_2020-01-21-09-15-21.db ";		
		DumpFile d = new DumpFile(filePath);	
		Map<String, Object> f = d.getInfo();				
		assertEquals(2L, f.get("CorruptFrameCount"));
			
	}
	
	

}
