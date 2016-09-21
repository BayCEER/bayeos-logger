package bayoes.binary;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import bayeos.binary.CheckSum;


public class CheckSumTest {
	
	CheckSum chk;
	
	@Before
	public void setUp(){
		chk = new CheckSum();
	}


	@Test
	public void testOneByte() {		
		chk.addBytes(new byte[]{0x1, 0x1, 0x50, 0x1, 0x0, 0x48, 0x65, 0x6C, 0x6C, 0x6F});
		assertEquals(0xB8, chk.oneByte());
	}
	

	
	
	@Test
	public void test2Byte(){
		chk.addBytes(new byte[]{0xf, 0x1, 0x44, 0x1, 0x1, 0x2, 0x1, 0x3, 0x1, 0x4, 0x1});
		assertEquals(65437, chk.twoByte());
		
		
	}
	
}
