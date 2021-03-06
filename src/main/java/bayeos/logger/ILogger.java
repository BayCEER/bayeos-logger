package bayeos.logger;

import java.io.IOException;
import java.util.Date;

public interface ILogger {
		
	public String getName() throws IOException;
	public void setName(String name) throws IOException;

	public Date getTime() throws IOException;
	public Date setTime(Date date) throws IOException;

	public int getLoggingInterval() throws IOException;
	public int setLoggingInterval(int interval) throws IOException;

	public Date getDateOfNextFrame() throws IOException;	
	public String getVersion() throws IOException;	
	public Boolean getBatteryStatus() throws IOException;


		
	public void startLiveData() throws IOException;
	
	public void stopMode() throws IOException;
	
	public void breakSocket() throws IOException;
	
	
	@Deprecated
	public long startData(byte dataMode) throws IOException;
	@Deprecated
	public void stopData(byte stopMode) throws IOException;	
	@Deprecated
	public void stopLiveData() throws IOException;
		
	
	/**
	 * Reads data in non blocking mode 
	 * @return data|null
	 * @throws IOException
	 */
	public byte[] readData() throws IOException;

	
	
	

	/* 
	 * @version 1.1
	 */
	public long startBulkData(byte dataMode) throws IOException;
	/*
	 * @version 1.1
	 */
	public byte[] readBulk() throws IOException;
	/*
	 * @version 1.1
	 */
	public void sendBufferCommand(byte command) throws IOException;
		
	
	
	
	

	
	

	

	

	

}