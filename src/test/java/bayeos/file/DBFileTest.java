package bayeos.file;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;


public class DBFileTest {

	@Test
	public void testRead() throws IOException {
		//InputStream in = this.getClass().getResourceAsStream("/flm1.db");		
		InputStream in = new FileInputStream("c:\\Users\\oliver\\dumps\\BAYEOS.001");
		DBFile db = new DBFile("router");
		db.read(in);				
		in.close();		
	}
	
	
}
