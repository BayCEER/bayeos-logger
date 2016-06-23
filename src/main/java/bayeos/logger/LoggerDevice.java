package bayeos.logger;

import static bayeos.frame.FrameConstants.*;
import static bayeos.logger.LoggerConstants.*;
import static bayeos.serialframe.SerialFrameConstants.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;
import bayeos.serialframe.SerialFrameDevice;

public class LoggerDevice implements ILogger {
			
	private SerialFrameDevice dev;
	
	public LoggerDevice(SerialFrameDevice dev){
		this.dev = dev;
	}
	
	public void close() {
		if (this.dev != null){
			this.dev.close();
		}
	}
	
	@Override
	public String getName() throws IOException {		
		dev.writeFrame(api_data, new byte[]{Command,GetName});
		byte[] resp = readCommandResponse(GetName);		
		return new String(resp);		
	}
	
	@Override
	public void setName(String name) throws IOException {		
		ByteBuffer bf = ByteBuffer.allocate(name.length()+2);
		bf.put(Command);
		bf.put(SetName);
		bf.put(name.getBytes());						
		dev.writeFrame(api_data,bf.array());				
		readCommandResponse(SetName);		
	}


	@Override
	public Date getTime() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,GetTime});
		byte[] resp = readCommandResponse(GetTime);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));		
	}


	@Override
	public Date setTime(Date date) throws IOException {
		long secs = DateAdapter.getSeconds(date);		
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetTime);
		bf.put(ByteArray.toByteUInt32(secs));
		dev.writeFrame(api_data, bf.array());		
		byte[] resp = readCommandResponse(SetTime);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));		
	}


	@Override
	public int getSamplingInterval() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,GetSamplingInt});
		byte[] resp = readCommandResponse(GetSamplingInt);
		return ByteArray.fromByteInt16(resp);
		
	}


	@Override
	public void setSamplingInterval(int interval) throws IOException {
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetSamplingInt);
		bf.put(ByteArray.toByteInt16((short)interval));
		dev.writeFrame(api_data,bf.array());
		
		readCommandResponse(SetSamplingInt);	
	}


	@Override
	public Date getDateOfNextFrame() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,GetTimeOfNextFrame});
		byte[] resp = readCommandResponse(GetTimeOfNextFrame);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));
	}


	@Override
	public long startData(int dataMode) throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,StartData,(byte)dataMode});
		byte[] resp = readCommandResponse(StartData);
		return ByteArray.fromByteUInt32(resp);
	}


	@Override
	public void stopData(int stopMode) throws IOException {
		breakSocket();
		dev.writeFrame(api_data, new byte[]{Command,StopData,(byte)stopMode});
		readCommandResponse(StopData);
	}


	@Override
	public void startLiveData() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,StartLiveData});
		readCommandResponse(StartLiveData);				
	}


	@Override
	public void stopLiveData() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,StopLiveData});
		readCommandResponse(StopLiveData);	
		
	}


	@Override
	public String getVersion() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,GetVersion});
		byte[] resp = readCommandResponse(GetVersion);		
		return new String(resp);
	}


	@Override
	public byte[] readData() throws IOException {		
		return dev.readFrame();
	}


	@Override
	public void breakSocket() throws IOException {
		dev.writeFrame(api_ack, new byte[]{ack_break});		
	}


	@Override
	public void stopMode() throws IOException {
		dev.writeFrame(api_data, new byte[]{Command,ModeStop});
		readCommandResponse(ModeStop);		
	}


	@Override
	public long startBulkData(int dataMode) throws IOException {
		if (dataMode==DM_FULL){
			dev.writeFrame(api_data, new byte[]{Command,StartBulkData});
			byte[] resp = readCommandResponse(StartBulkData);
			return ByteArray.fromByteUInt32(resp);			
		} else if (dataMode == DM_NEW){
			dev.writeFrame(api_data, new byte[]{Command,BufferCommand,BC_GET_READ_POS});
			byte[] resp = readCommandResponse(BufferCommand);						
			ByteBuffer bf = ByteBuffer.allocate(6);
			bf.put(Command);
			bf.put(StartBulkData);
			bf.put(resp);			
			dev.writeFrame(api_data,bf.array());								
			byte[] db = readCommandResponse(StartBulkData);
			return  ByteArray.fromByteUInt32(db);
		} else {
			throw new IOException("Mode not supported."); 	
		}
	}


	@Override
	public byte[] readBulk() throws IOException {
		return dev.readFrame();
	}


	@Override
	public void sendBufferCommand(int command) throws IOException {
		dev.writeFrame(api_data, new byte[]{BufferCommand,(byte)command});		
		readCommandResponse(BufferCommand);
		
	}
	
	private byte[] readCommandResponse(byte command) throws IOException {		
		byte[] resp = dev.readFrame();
		if (resp.length < 2){
			throw new IOException("Invalid command response (too short).");			
		} else {
			if (resp[0] != Response || resp[1] != command){
				throw new IOException("Invalid command response (invalid command).");
			} else {
				return Arrays.copyOfRange(resp, 2, resp.length);	
			}			
		}
	}
	
	
		
	
	
}

