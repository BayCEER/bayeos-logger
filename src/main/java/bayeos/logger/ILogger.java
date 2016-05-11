package bayeos.logger;

import java.io.IOException;
import java.util.Date;

public interface ILogger {
	
	// BufferCommands
	public static final int BC_SAVE_READ_TO_EPROM = 0;
	public static final int BC_ERASE = 1;
	public static final int BC_SET_READ_TO_LAST_EPROM_POS = 2;
	public static final int BC_SET_READ_TO_WRITE_POINTER = 3;
	public static final int BC_SET_READ_TO_LAST_OF_BINARY_END_POS = 4;
	public static final int BC_GET_READ_POS = 5;
		
	// DataMode 
	public static final int DM_NEW = 0;
	public static final int DM_FULL = 0;
	
	// StopMode 
	public static final int SM_STOP = 0;
	public static final int SM_RESET = 1;
	public static final int SM_CANCEL = 2;

	public String getName() throws IOException;
	public void setName(String name) throws IOException;

	public Date getTime() throws IOException;
	public Date setTime(Date date) throws IOException;

	public int getSamplingInterval() throws IOException;
	public void setSamplingInterval(int interval) throws IOException;

	public Date getDateOfNextFrame() throws IOException;

	public long startData(int dataMode) throws IOException;	
	public void stopData(int stopMode) throws IOException;
		
	public void startLiveData() throws IOException;
	public void stopLiveData() throws IOException;
	
	public String getVersion() throws IOException;

	public int[] readData() throws IOException;

	public void breakSocket() throws IOException;
	
	public void stopMode() throws IOException;

	/* 
	 * @version 1.1
	 */
	public long startBulkData(int dataMode) throws IOException;
	/*
	 * @version 1.1
	 */
	public int[] readBulk() throws IOException;
	/*
	 * @version 1.1
	 */
	public void sendBufferCommand(int command) throws IOException;
	
	
	

	
	

	

	

	

}