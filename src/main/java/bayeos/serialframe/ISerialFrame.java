package bayeos.serialframe;

import java.io.IOException;




/**
 * A frame based interface to communicate over serial lines 
 * 
 * @author oliver
 *
 */

public interface ISerialFrame {
				
		
		/** 
		 * Writes a payload to device as a serial frame 
		 * @param payload 
		 * @throws IOException 
		 */
		public void writeFrame(byte[] data) throws IOException;
		
						
		
		/**
		 * Reads a serial frame from device  
		 * @return payload 
		 * @throws IOException 
		 */

		public byte[] readFrame() throws IOException;
										

		
		/**
		 * Sends a break 
		 * @throws IOException
		 */
		public void breakFrame() throws IOException;
		
		
		
}
