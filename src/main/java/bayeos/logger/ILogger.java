package bayeos.logger;

import java.io.IOException;
import java.util.Date;

public interface ILogger {
	
	

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
	
	@Deprecated
	public void stopLiveData() throws IOException;
	
	
	public String getVersion() throws IOException;

	public byte[] readData() throws IOException;

	public void breakSocket() throws IOException;
	
	public void stopMode() throws IOException;

	/* 
	 * @version 1.1
	 */
	public long startBulkData(int dataMode) throws IOException;
	/*
	 * @version 1.1
	 */
	public byte[] readBulk() throws IOException;
	/*
	 * @version 1.1
	 */
	public void sendBufferCommand(int command) throws IOException;
	
    
	
	
	
	

	
	

	

	

	

}