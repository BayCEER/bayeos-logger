package bayoes.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import bayeos.binary.CheckSum;


public class CheckSumTest {

	@Test
	public void testCalculateOk() {		
		CheckSum chk = new CheckSum();
		chk.addBytes(new int[]{0x1, 0x1, 0x50, 0x1, 0x0, 0x48, 0x65, 0x6C, 0x6C, 0x6F});
		assertEquals(0xB8, chk.get());	
		
		CheckSum chk1 = new CheckSum();
		chk1.addBytes(new int[]{0x2,0x1});				
		assertEquals(0xfc, chk1.get());
		
		CheckSum chk2 = new CheckSum();
		chk2.addByte(0x2);
		chk2.addByte(0x1);
		assertEquals(0xfc, chk2.get());
		
		
	}
	
	
	
	
	
	

}
