package bayeos.serialframe;

import java.io.IOException;

import bayeos.frame.callback.ReadCallBack;




/**
 * A frame based interface to communicate over serial lines 
 * 
 * @author oliver
 *
 */

public interface SerialFrameInterface {
				
		
		/** 
		 * Writes a payload to device as a serial frame 
		 * @param apiType : @see {@link SerialFrameConstants}  
		 * @param data: payload 
		 * @throws IOException 
		 */
		void writeFrame(byte apiType, byte[] data) throws IOException;		

		public byte[] readFrame() throws IOException;
		public void readFrame(ReadCallBack<?> callback, boolean blocking) ;
		public void readFrame(ReadCallBack<?> callback);
		
		public void stop();		
		public void start();


}
