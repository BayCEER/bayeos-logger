package bayeos.serialframe;

import java.io.IOException;




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
		 */
		void writeFrame(byte apiType, byte[] data);
		
		public byte[] readFrame() throws IOException;
		public void readFrame(ReadCallback<?> callback, boolean blocking);
		public void readFrame(ReadCallback<?> callback);
		
		
		/**
		 * A callback interface to get response back from serial frame device 
		 * @author oliver
		 *
		 */
		public interface ReadCallback<T>
		{			
			public void onData(byte[] data);
			public void onError(String msg);
			public void onAck(byte value);	
			
			public T getValue();	
			public boolean hasError();
			public String getErrorMsg();
			public byte getAck();
			
			public boolean isRunning();
		}
		
		
		
		
		
		public void stop();		
		public void start();


}
