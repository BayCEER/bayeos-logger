package bayeos.logger.dump;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class DumpFileRepositoryTest {

	String filePath = "src/test/resources";
	
	@Test
	public void testReadItems() throws IOException{
		List<DumpFile> files = DumpFileRepository.getFiles(filePath);		
		assertEquals(3, files.size());		
		DumpFile d = files.get(2);		
		assertEquals("DummyC", d.getOrigin());		
	}

	

}
