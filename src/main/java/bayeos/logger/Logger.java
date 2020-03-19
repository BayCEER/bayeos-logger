package bayeos.logger;

import static bayeos.frame.FrameConstants.Command;
import static bayeos.frame.FrameConstants.CommandResponse;
import static bayeos.logger.LoggerConstants.BC_GET_READ_POS;
import static bayeos.logger.LoggerConstants.BufferCommand;
import static bayeos.logger.LoggerConstants.DM_FULL;
import static bayeos.logger.LoggerConstants.DM_NEW;
import static bayeos.logger.LoggerConstants.GetBatteryStatus;
import static bayeos.logger.LoggerConstants.GetName;
import static bayeos.logger.LoggerConstants.GetSamplingInt;
import static bayeos.logger.LoggerConstants.GetTime;
import static bayeos.logger.LoggerConstants.GetTimeOfNextFrame;
import static bayeos.logger.LoggerConstants.GetVersion;
import static bayeos.logger.LoggerConstants.ModeStop;
import static bayeos.logger.LoggerConstants.SetName;
import static bayeos.logger.LoggerConstants.SetSamplingInt;
import static bayeos.logger.LoggerConstants.SetTime;
import static bayeos.logger.LoggerConstants.StartBulkData;
import static bayeos.logger.LoggerConstants.StartData;
import static bayeos.logger.LoggerConstants.StartLiveData;
import static bayeos.logger.LoggerConstants.StopData;
import static bayeos.logger.LoggerConstants.StopLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;
import bayeos.serialdevice.ISerialDevice;
import bayeos.serialdevice.SerialDevice;
import bayeos.serialframe.SerialFrameDevice;

public class Logger implements ILogger {
					
	private SerialFrameDevice dev;
	
	public float version = 1.4F;
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Logger.class);
	
	public Logger(ISerialDevice serial){
		dev = new SerialFrameDevice(serial);
	}
	
	
	public Logger(InputStream in, OutputStream out){
		this(new SerialDevice(in, out));
	}
	
			
	@Override
	public String getName() throws IOException {	
		log.debug("Getting name of device");	
		dev.writeFrame(new byte[]{Command,GetName});
		byte[] resp = readCommandResponse(GetName);		
		return new String(resp);		
	}
	
	@Override
	public void setName(String name) throws IOException {	
		log.debug("Setting name of device");
		ByteBuffer bf = ByteBuffer.allocate(name.length()+2);
		bf.put(Command);
		bf.put(SetName);
		bf.put(name.getBytes());						
		dev.writeFrame(bf.array());				
		byte[] resp = readCommandResponse(SetName);
		String newName = new String(resp); 
		if (!newName.equals(name)) {
			throw new IOException("Expected:" + name + " was:" + newName);
		}
	}


	@Override
	public Date getTime() throws IOException {
		log.debug("Getting time of device");
		dev.writeFrame(new byte[]{Command,GetTime});
		byte[] resp = readCommandResponse(GetTime);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));		
	}


	@Override
	public Date setTime(Date date) throws IOException {
		log.debug("Setting time of device to " + date);
		long secs = DateAdapter.getSeconds(date);		
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetTime);
		bf.put(ByteArray.toByteUInt32(secs));
		dev.writeFrame(bf.array());		
		byte[] resp = readCommandResponse(SetTime);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));		
	}


	@Override
	public int getLoggingInterval() throws IOException {
		log.debug("Getting sampling interval");
		dev.writeFrame(new byte[]{Command,GetSamplingInt});
		byte[] resp = readCommandResponse(GetSamplingInt);
		return ByteArray.fromByteInt16(resp);		
	}

	

	@Override
	public int setLoggingInterval(int interval) throws IOException {
		log.debug("Set sampling interval");
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetSamplingInt);
		bf.put(ByteArray.toByteInt16((short)interval));
		dev.writeFrame(bf.array());		
		byte[] resp = readCommandResponse(SetSamplingInt);
		return ByteArray.fromByteInt16(resp);
	}


	@Override
	public Date getDateOfNextFrame() throws IOException {
		log.debug("Get date of next frame");
		dev.writeFrame(new byte[]{Command,GetTimeOfNextFrame});
		byte[] resp = readCommandResponse(GetTimeOfNextFrame);
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));
	}


	@Override
	public long startData(byte dataMode) throws IOException {
		log.debug("Starting data dump.");
		dev.writeFrame(new byte[]{Command,StartData,dataMode});
		byte[] resp = readCommandResponse(StartData);
		return ByteArray.fromByteUInt32(resp);
	}


	@Override
	public void stopData(byte stopMode) throws IOException {
		log.debug("Stopping data dump.");	
		breakSocket();
		dev.writeFrame(new byte[]{Command,StopData,(byte)stopMode});
		readCommandResponse(StopData);
	}


	@Override
	public void startLiveData() throws IOException {
		log.debug("Start live data");
		dev.writeFrame(new byte[]{Command,StartLiveData});
		readCommandResponse(StartLiveData);				
	}


	@Override
	public void stopLiveData() throws IOException {
		log.debug("Stop live data");
		if (version >= 1.3F) {
			dev.writeFrame(new byte[]{Command,ModeStop});
			readCommandResponse(ModeStop);				
		} else {
			dev.writeFrame(new byte[]{Command,StopLiveData});
			readCommandResponse(StopLiveData);				
		}
		
	}


	@Override
	public String getVersion() throws IOException {
		log.debug("Get version");
		dev.writeFrame(new byte[]{Command,GetVersion});
		byte[] resp = readCommandResponse(GetVersion);
		if (resp != null) {			
			this.version = Float.valueOf(new String(resp));	
		}		
		return new String(resp);
	}


	@Override
	public byte[] readData() throws IOException {
		if (log.isDebugEnabled()) log.debug("readData");				
		return dev.readFrame();
				 
	}


	@Override
	public void breakSocket() throws IOException {
		log.debug("breakSocket");
		dev.breakFrame();		
	}


	@Override
	public void stopMode() throws IOException {
		log.debug("stopMode");
		dev.writeFrame(new byte[]{Command,ModeStop});
		readCommandResponse(ModeStop);		
	}


	@Override
	public long startBulkData(byte dataMode) throws IOException {
		log.debug("startBulkData mode:" +dataMode);
		if (dataMode==DM_FULL){
			dev.writeFrame(new byte[]{Command,StartBulkData});
			byte[] resp = readCommandResponse(StartBulkData);
			return ByteArray.fromByteUInt32(resp);			
		} else if (dataMode == DM_NEW){
			dev.writeFrame(new byte[]{Command,BufferCommand,BC_GET_READ_POS});
			byte[] resp = readCommandResponse(BufferCommand);						
			ByteBuffer bf = ByteBuffer.allocate(6);
			bf.put(Command);
			bf.put(StartBulkData);
			bf.put(resp);			
			dev.writeFrame(bf.array());								
			byte[] db = readCommandResponse(StartBulkData);
			return ByteArray.fromByteUInt32(db);
		} else {
			throw new IOException("Mode not supported."); 	
		}
	}


	@Override
	public byte[] readBulk() throws IOException {
		log.debug("readBulk");
		return dev.readFrame();
	}


	@Override
	public void sendBufferCommand(byte command) throws IOException {
		log.debug("sendBufferCommand command:" + ByteArray.toString(command));
		dev.writeFrame(new byte[]{Command,BufferCommand,command});		
		readCommandResponse(BufferCommand);		
	}
	
	private byte[] readCommandResponse(byte command) throws IOException {
		log.debug("readCommandResponse command:" + ByteArray.toString(command));
		
		byte[] resp = dev.readFrame();
		if (resp.length < 2){
			throw new IOException("Invalid command response (too short).");			
		} else {
			if (resp[0] != CommandResponse || resp[1] != command){				
				throw new IOException("Invalid command response (invalid command).");
				
			} else {
				return Arrays.copyOfRange(resp, 2, resp.length);	
			}			
		}
	}


	@Override
	public Boolean getBatteryStatus() throws IOException {
		log.debug("getBatteryStatus()");
		if (version >= 1.4F) {
			dev.writeFrame(new byte[]{Command,GetBatteryStatus});
			byte[] resp = readCommandResponse(GetBatteryStatus);			
			ByteBuffer bf = ByteBuffer.wrap(resp);
			bf.order(ByteOrder.LITTLE_ENDIAN);			
			int mv = Short.toUnsignedInt(bf.getShort()); 
			int limit = Short.toUnsignedInt(bf.getShort()); 			
			if (mv == 0 || limit == 0) { 
				return null;
			} else {
				return mv > limit;	
			}					
		} else {
			return null;
		}		
	}


	
	
}

