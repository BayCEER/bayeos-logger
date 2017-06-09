package bayeos.serialdevice;

import java.io.IOException;


/**
 * A hardware independent serial device
 * 
 * @author oliver
 * 
 */

public interface ISerialDevice {
		
	
	/**
	 * Reads a byte from input  
	 * @return byte or -1 
	 * @throws IOException
	 */		
	public int read() throws IOException;
	
		
	
	/**
	 * Writes bytes of data to output 
	 * @param data payload 
	 * @throws IOException
	 */	
	public void write(byte[] data) throws IOException;
	
	
	
	

}
