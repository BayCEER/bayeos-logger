package bayeos.device;

import java.io.IOException;


/**
 * A hardware independent serial device
 * 
 * @author oliver
 * 
 */

public interface SerialDeviceInterface {
		
	
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
	
	
	
	
	/** 
	 * Opens connection and initialize device 
	 * @return true on success
	 */
	public boolean open();
	
	
	/**
	 * Close connection to device
	 */
	public void close();

}
