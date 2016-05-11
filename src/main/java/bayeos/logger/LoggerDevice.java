package bayeos.logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;
import bayeos.frame.FrameConstants;
import bayeos.frame.callback.DateCallBack;
import bayeos.frame.callback.StringCallBack;
import bayeos.serialframe.SerialFrameDevice;
import static bayeos.serialframe.SerialFrameConstants.api_data;

public class LoggerDevice implements ILogger {
	
	
			
	private SerialFrameDevice dev;
	
	public LoggerDevice(SerialFrameDevice dev){
		this.dev = dev;
	}
	
	
	@Override
	public String getName() throws IOException {		
		dev.writeFrame(api_data, new byte[]{FrameConstants.Command,LoggerConstants.GetName});
		StringCallBack cb = new StringCallBack();		
		dev.readFrame(cb);		
		if (cb.hasError()){
			throw new IOException(cb.getErrorMsg());
		} else {
			return cb.getValue();	
		}
	}


	@Override
	public void setName(String name) throws IOException {		
		ByteBuffer bf = ByteBuffer.allocate(name.length()+2);
		bf.put(FrameConstants.Command);
		bf.put(LoggerConstants.SetName);
		bf.put(name.getBytes());						
		dev.writeFrame(api_data,bf.array());
	}


	@Override
	public Date getTime() throws IOException {
		dev.writeFrame(api_data, new byte[]{FrameConstants.Command,LoggerConstants.GetTime});
		DateCallBack cb = new DateCallBack();		
		dev.readFrame(cb);		
		if (cb.hasError()){
			throw new IOException(cb.getErrorMsg());
		} else {
			return cb.getValue();	
		}
	}


	@Override
	public Date setTime(Date date) throws IOException {
		long secs = DateAdapter.getSeconds(date);
		
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(FrameConstants.Command);
		bf.put(LoggerConstants.SetTime);
		bf.put(ByteArray.toByteUInt32(secs));
		
		dev.writeFrame(api_data, bf.array());
				
		DateCallBack dc = new DateCallBack();
		dev.readFrame(dc);
		if (dc.hasError()){
			throw new IOException(dc.getErrorMsg());
		} else {
			return dc.getValue();	
		}
		
	}


	@Override
	public int getSamplingInterval() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setSamplingInterval(int interval) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Date getDateOfNextFrame() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long startData(int dataMode) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void stopData(int stopMode) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startLiveData() throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopLiveData() throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getVersion() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int[] readData() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void breakSocket() throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopMode() throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public long startBulkData(int dataMode) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int[] readBulk() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void sendBufferCommand(int command) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}
